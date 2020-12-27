package net.smoofyuniverse.keyring.gnome;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface GNOMEKeyringLib extends Library {
	GNOMEKeyringLib INSTANCE = Native.load("gnome-keyring", GNOMEKeyringLib.class);

	int gnome_keyring_unlock_sync(String keyring, String password);

	int gnome_keyring_item_get_info_full_sync(String keyring, int id, int flags, PointerByReference item_info);

	void gnome_keyring_item_info_free(Pointer item_info);

	String gnome_keyring_item_info_get_secret(Pointer item_info);

	String gnome_keyring_result_to_message(int r);

	int gnome_keyring_set_network_password_sync(String keyring, String user, String domain, String server,
												String object, String protocol, String authtype, int port, String password, IntByReference item_id);
}
