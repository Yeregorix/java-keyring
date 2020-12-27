package net.smoofyuniverse.keyring.gnome;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface GLIB2 extends Library {
	GLIB2 INSTANCE = Native.load("glib-2.0", GLIB2.class);

	void g_set_application_name(String string);
}
