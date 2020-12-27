package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Crypt32Util;
import net.smoofyuniverse.keyring.KeyringBackend;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.util.ServiceAndAccount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Keyring backend which uses Windows DPAPI
 */
public class WindowsDPAPIBackend extends KeyringBackend {

	@Override
	public String getID() {
		return "WindowsDPAPI";
	}

	@Override
	public boolean isSupported() {
		return Platform.isWindows();
	}

	@Override
	public boolean isKeyStorePathRequired() {
		return true;
	}

	@Override
	public String getPassword(String service, String account) throws PasswordAccessException {
		ServiceAndAccount.validate(service, account);

		Map<ServiceAndAccount, byte[]> map;
		try {
			map = loadEntries();
		} catch (Exception e) {
			throw new PasswordAccessException("Failed to load entries from the keystore", e);
		}

		byte[] bytes = map.get(new ServiceAndAccount(service, account));
		if (bytes == null)
			return null;

		if (bytes.length == 0)
			return "";

		try {
			return new String(Crypt32Util.cryptUnprotectData(bytes), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new PasswordAccessException("Failed to decrypt password", e);
		}
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		ServiceAndAccount.validate(service, account);

		Map<ServiceAndAccount, byte[]> map;
		try {
			map = loadEntries();
		} catch (Exception e) {
			throw new PasswordAccessException("Failed to load entries from the keystore", e);
		}

		byte[] bytes;
		if (password.isEmpty()) {
			bytes = new byte[0];
		} else {
			try {
				bytes = Crypt32Util.cryptProtectData(password.getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				throw new PasswordAccessException("Failed to encrypt password", e);
			}
		}

		map.put(new ServiceAndAccount(service, account), bytes);

		try {
			saveEntries(map);
		} catch (Exception e) {
			throw new PasswordAccessException("Failed to save password entries to the keystore", e);
		}
	}

	private Map<ServiceAndAccount, byte[]> loadEntries() throws IOException {
		Map<ServiceAndAccount, byte[]> map = new HashMap<>();
		if (Files.exists(this.keyStorePath) && Files.size(this.keyStorePath) != 0) {
			try (DataInputStream in = new DataInputStream(Files.newInputStream(this.keyStorePath))) {
				int count = in.readInt();
				for (int i = 0; i < count; i++) {
					ServiceAndAccount sa = ServiceAndAccount.read(in);
					byte[] bytes = new byte[in.readInt()];
					in.readFully(bytes);
					map.put(sa, bytes);
				}
			}
		}
		return map;
	}

	private void saveEntries(Map<ServiceAndAccount, byte[]> map) throws IOException {
		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(this.keyStorePath))) {
			out.writeInt(map.size());
			for (Entry<ServiceAndAccount, byte[]> e : map.entrySet()) {
				e.getKey().write(out);
				out.writeInt(e.getValue().length);
				out.write(e.getValue());
			}
		}
	}
}
