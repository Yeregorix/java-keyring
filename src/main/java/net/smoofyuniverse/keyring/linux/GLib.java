package net.smoofyuniverse.keyring.linux;

import com.sun.jna.Library;
import net.smoofyuniverse.keyring.util.NativeUtil;

public interface GLib extends Library {
	GLib INSTANCE = NativeUtil.loadOrNull("glib-2.0", GLib.class);

	void g_error_free(GError error);
}
