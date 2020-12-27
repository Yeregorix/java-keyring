package net.east301.keyring.gnome;

import com.sun.jna.Platform;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import net.east301.keyring.BackendNotSupportedException;
import net.east301.keyring.KeyringBackend;
import net.east301.keyring.PasswordRetrievalException;
import net.east301.keyring.PasswordSaveException;
import net.east301.keyring.util.ServiceAndAccount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Keyring backend which uses GNOME Keyring
 */
public class GNOMEKeyringBackend extends KeyringBackend {

	@Override
	public String getID() {
		return "GNOMEKeyring";
	}

	@Override
	public void setup() throws BackendNotSupportedException {
		NativeLibraryManager.loadNativeLibraries();

		int result = NativeLibraryManager.gklib.gnome_keyring_unlock_sync(null, null);
		if (result != 0)
			throw new BackendNotSupportedException(NativeLibraryManager.gklib.gnome_keyring_result_to_message(result));
	}

	@Override
	public boolean isSupported() {
		return Platform.isLinux();
	}

	@Override
	public boolean isKeyStorePathRequired() {
		return true;
	}

	@Override
	public String getPassword(String service, String account) throws PasswordRetrievalException {
		ServiceAndAccount.validate(service, account);

		Map<ServiceAndAccount, Integer> map;
		try {
			map = loadEntries();
		} catch (Exception e) {
			throw new PasswordRetrievalException("Failed to load entries from the keystore", e);
		}

		Integer id = map.get(new ServiceAndAccount(service, account));
		if (id == null)
			return null;

		PointerByReference item_info = new PointerByReference();
		try {
			int result = NativeLibraryManager.gklib.gnome_keyring_item_get_info_full_sync(null, id, 1, item_info);
			if (result == 0)
				return NativeLibraryManager.gklib.gnome_keyring_item_info_get_secret(item_info.getValue());

			throw new PasswordRetrievalException(NativeLibraryManager.gklib.gnome_keyring_result_to_message(result));
		} finally {
			NativeLibraryManager.gklib.gnome_keyring_item_info_free(item_info.getValue());
		}
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordSaveException {
		ServiceAndAccount.validate(service, account);

		Map<ServiceAndAccount, Integer> map;
		try {
			map = loadEntries();
		} catch (Exception e) {
			throw new PasswordSaveException("Failed to load entries from the keystore", e);
		}

		IntByReference item_id = new IntByReference();
		int result = NativeLibraryManager.gklib.gnome_keyring_set_network_password_sync(null, account, null, service, null, null, null, 0, password, item_id);
		if (result != 0)
			throw new PasswordSaveException(NativeLibraryManager.gklib.gnome_keyring_result_to_message(result));

		map.put(new ServiceAndAccount(service, account), item_id.getValue());

		try {
			saveEntries(map);
		} catch (Exception e) {
			throw new PasswordSaveException("Failed to save entries to the keystore", e);
		}
	}

	private Map<ServiceAndAccount, Integer> loadEntries() throws IOException {
		Map<ServiceAndAccount, Integer> map = new HashMap<>();
		if (Files.exists(this.keyStorePath) && Files.size(this.keyStorePath) != 0) {
			try (DataInputStream in = new DataInputStream(Files.newInputStream(this.keyStorePath))) {
				int count = in.readInt();
				for (int i = 0; i < count; i++)
					map.put(ServiceAndAccount.read(in), in.readInt());
			}
		}
		return map;
	}

	private void saveEntries(Map<ServiceAndAccount, Integer> map) throws IOException {
		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(this.keyStorePath))) {
			out.writeInt(map.size());
			for (Entry<ServiceAndAccount, Integer> e : map.entrySet()) {
				e.getKey().write(out);
				out.writeInt(e.getValue());
			}
		}
	}
}
