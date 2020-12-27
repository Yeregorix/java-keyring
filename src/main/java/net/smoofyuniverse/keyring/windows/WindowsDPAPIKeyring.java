package net.smoofyuniverse.keyring.windows;

import com.sun.jna.platform.win32.Crypt32;
import com.sun.jna.platform.win32.Crypt32Util;
import net.smoofyuniverse.keyring.Keyring;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.UnsupportedBackendException;
import net.smoofyuniverse.keyring.util.ServiceAccountPair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * A keyring using Windows DPAPI.
 */
public class WindowsDPAPIKeyring implements Keyring {
	private final Path keyStore;

	/**
	 * Creates a new keyring using Windows DPAPI.
	 *
	 * @param keyStore The key store used to save encrypted passwords.
	 * @throws UnsupportedBackendException if the backend for this implementation is not available.
	 */
	public WindowsDPAPIKeyring(Path keyStore) throws UnsupportedBackendException {
		if (keyStore == null)
			throw new IllegalArgumentException("keyStore");
		this.keyStore = keyStore;

		try {
			Objects.requireNonNull(Crypt32.INSTANCE);
		} catch (Throwable t) {
			throw new UnsupportedBackendException("Failed to load native library", t);
		}
	}

	@Override
	public String getBackendName() {
		return "WindowsDPAPI";
	}

	@Override
	public String getPassword(String service, String account) throws PasswordAccessException {
		ServiceAccountPair.validate(service, account);

		Map<ServiceAccountPair, byte[]> map = loadEntries();

		byte[] bytes = map.get(new ServiceAccountPair(service, account));
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
		ServiceAccountPair.validate(service, account);

		Map<ServiceAccountPair, byte[]> map = loadEntries();

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

		map.put(new ServiceAccountPair(service, account), bytes);

		saveEntries(map);
	}

	private Map<ServiceAccountPair, byte[]> loadEntries() throws PasswordAccessException {
		Map<ServiceAccountPair, byte[]> map = new HashMap<>();
		try {
			if (Files.exists(this.keyStore) && Files.size(this.keyStore) != 0) {
				try (DataInputStream in = new DataInputStream(Files.newInputStream(this.keyStore))) {
					int count = in.readInt();
					for (int i = 0; i < count; i++) {
						ServiceAccountPair sa = ServiceAccountPair.read(in);
						byte[] bytes = new byte[in.readInt()];
						in.readFully(bytes);
						map.put(sa, bytes);
					}
				}
			}
		} catch (IOException e) {
			throw new PasswordAccessException("Failed to load entries from the keystore", e);
		}
		return map;
	}

	private void saveEntries(Map<ServiceAccountPair, byte[]> map) throws PasswordAccessException {
		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(this.keyStore))) {
			out.writeInt(map.size());
			for (Entry<ServiceAccountPair, byte[]> e : map.entrySet()) {
				e.getKey().write(out);
				out.writeInt(e.getValue().length);
				out.write(e.getValue());
			}
		} catch (IOException e) {
			throw new PasswordAccessException("Failed to save password entries to the keystore", e);
		}
	}
}
