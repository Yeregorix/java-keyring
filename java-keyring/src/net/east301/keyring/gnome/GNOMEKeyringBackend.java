package net.east301.keyring.gnome;

import com.sun.jna.Platform;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import net.east301.keyring.BackendNotSupportedException;
import net.east301.keyring.KeyringBackend;
import net.east301.keyring.PasswordRetrievalException;
import net.east301.keyring.PasswordSaveException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Keyring backend which uses GNOME Keyring
 */
public class GNOMEKeyringBackend extends KeyringBackend {
	
    @Override
    public void setup() throws BackendNotSupportedException {
        NativeLibraryManager.loadNativeLibraries();

		int result = NativeLibraryManager.gklib.gnome_keyring_unlock_sync(null, null);
		if (result != 0)
			throw new BackendNotSupportedException(NativeLibraryManager.gklib.gnome_keyring_result_to_message(result));
    }

    /**
     * Returns true when the backend is supported
     */
    @Override
    public boolean isSupported() {
        return Platform.isLinux();
    }

    /**
     * Returns true if the backend directory uses some file to store passwords
     */
    @Override
    public boolean isKeyStorePathRequired() {
        return true;
    }

    /**
     * Gets password from key store
     *
     * @param service   Service name
     * @param account   Account name
     *
     * @return  Password related to specified service and account
     *
     * @throws PasswordRetrievalException   Thrown when an error happened while getting password
     */
    @Override
    public String getPassword(String service, String account) throws PasswordRetrievalException {
		PointerByReference item_info = new PointerByReference();
//		Pointer item = null;

		Map<String, Integer> map;

		try {
			map = loadMap();
		} catch (Exception e) {
			throw new PasswordRetrievalException("Failed to load map from keystore", e);
		}

		Integer id = map.get(service + "/" + account);
		if (id == null)
			throw new PasswordRetrievalException("No password stored for this service and account.");

//		try {
			int result = NativeLibraryManager.gklib.gnome_keyring_item_get_info_full_sync(null, id, 1, item_info);
			if (result == 0)
				return NativeLibraryManager.gklib.gnome_keyring_item_info_get_secret(item_info.getValue());

			throw new PasswordRetrievalException(NativeLibraryManager.gklib.gnome_keyring_result_to_message(result));
/*		} finally {
			if (item != null)
				NativeLibraryManager.gklib.gnome_keyring_item_info_free(item);
		} */
    }

    /**
     * Sets password to key store
     *
     * @param service   Service name
     * @param account   Account name
     * @param password  Password
     *
     * @throws PasswordSaveException    Thrown when an error happened while saving the password
     */
    @Override
    public void setPassword(String service, String account, String password) throws PasswordSaveException {
    	IntByReference item_id = new IntByReference();
    	int result = NativeLibraryManager.gklib.gnome_keyring_set_network_password_sync(null, account, null, service, null, null, null, 0, password, item_id);
		if (result != 0)
			throw new PasswordSaveException(NativeLibraryManager.gklib.gnome_keyring_result_to_message(result));

		Map<String, Integer> map;
		try {
			map = loadMap();
		} catch (Exception e) {
			throw new PasswordSaveException("Failed to load password entries from the keystore", e);
		}

		map.put(service + "/" + account, item_id.getValue());

		try {
			saveMap(map);
		} catch (Exception e) {
			throw new PasswordSaveException("Failed to save password entries to the keystore", e);
		}
    }

    /**
     * Gets backend ID
     */
    @Override
    public String getID() {
        return "GNOMEKeyring";
    }

    @SuppressWarnings("unchecked")
	private Map<String, Integer> loadMap() throws Exception {
		if (Files.exists(this.keyStorePath) && Files.size(this.keyStorePath) > 0) {
			try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(this.keyStorePath))) {
				return (Map<String, Integer>) in.readObject();
			}
		}
		return new HashMap<>();
    }

    private void saveMap(Map<String, Integer> map) throws Exception {
    	try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(this.keyStorePath))) {
			out.writeObject(map);
			out.flush();
		}
    }
}
