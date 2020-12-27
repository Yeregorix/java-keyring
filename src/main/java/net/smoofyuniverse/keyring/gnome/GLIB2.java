package net.smoofyuniverse.keyring.gnome;

import com.sun.jna.Library;

interface GLIB2 extends Library {

	void g_set_application_name(String string);

}
