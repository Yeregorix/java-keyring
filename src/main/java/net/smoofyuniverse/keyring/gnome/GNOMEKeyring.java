package net.smoofyuniverse.keyring.gnome;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import net.smoofyuniverse.keyring.Keyring;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.UnsupportedBackendException;
import net.smoofyuniverse.keyring.util.ServiceAccountPair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Keyring using GNOME Keyring.
 */
public class GNOMEKeyring implements Keyring {
	private final Path keyStore;

	public GNOMEKeyring(Path keyStore) throws UnsupportedBackendException {
		if (keyStore == null)
			throw new IllegalArgumentException("keyStore");
		this.keyStore = keyStore;

		try {
			Objects.requireNonNull(GKLib.INSTANCE);
			Objects.requireNonNull(GLIB2.INSTANCE);
		} catch (Throwable t) {
			throw new UnsupportedBackendException("Failed to load native libraries", t);
		}

		int result = GKLib.INSTANCE.gnome_keyring_unlock_sync(null, null);
		if (result != 0)
			throw new UnsupportedBackendException(GKLib.INSTANCE.gnome_keyring_result_to_message(result));
	}

	@Override
	public String getBackendName() {
		return "GNOMEKeyring";
	}

	@Override
	public String getPassword(String service, String account) throws PasswordAccessException {
		ServiceAccountPair.validate(service, account);

		Map<ServiceAccountPair, Integer> map = loadEntries();

		Integer id = map.get(new ServiceAccountPair(service, account));
		if (id == null)
			return null;

		PointerByReference item_info = new PointerByReference();
		try {
			int result = GKLib.INSTANCE.gnome_keyring_item_get_info_full_sync(null, id, 1, item_info);
			if (result == 0)
				return GKLib.INSTANCE.gnome_keyring_item_info_get_secret(item_info.getValue());

			throw new PasswordAccessException(GKLib.INSTANCE.gnome_keyring_result_to_message(result));
		} finally {
			GKLib.INSTANCE.gnome_keyring_item_info_free(item_info.getValue());
		}
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		ServiceAccountPair.validate(service, account);

		Map<ServiceAccountPair, Integer> map = loadEntries();

		IntByReference item_id = new IntByReference();
		int result = GKLib.INSTANCE.gnome_keyring_set_network_password_sync(null, account, null, service, null, null, null, 0, password, item_id);
		if (result != 0)
			throw new PasswordAccessException(GKLib.INSTANCE.gnome_keyring_result_to_message(result));

		map.put(new ServiceAccountPair(service, account), item_id.getValue());

		saveEntries(map);
	}

	private Map<ServiceAccountPair, Integer> loadEntries() throws PasswordAccessException {
		Map<ServiceAccountPair, Integer> map = new HashMap<>();
		try {
			if (Files.exists(this.keyStore) && Files.size(this.keyStore) != 0) {
				try (DataInputStream in = new DataInputStream(Files.newInputStream(this.keyStore))) {
					int count = in.readInt();
					for (int i = 0; i < count; i++)
						map.put(ServiceAccountPair.read(in), in.readInt());
				}
			}
		} catch (IOException e) {
			throw new PasswordAccessException("Failed to load entries from the keystore", e);
		}
		return map;
	}

	private void saveEntries(Map<ServiceAccountPair, Integer> map) throws PasswordAccessException {
		try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(this.keyStore))) {
			out.writeInt(map.size());
			for (Entry<ServiceAccountPair, Integer> e : map.entrySet()) {
				e.getKey().write(out);
				out.writeInt(e.getValue());
			}
		} catch (IOException e) {
			throw new PasswordAccessException("Failed to save entries to the keystore", e);
		}
	}
}
