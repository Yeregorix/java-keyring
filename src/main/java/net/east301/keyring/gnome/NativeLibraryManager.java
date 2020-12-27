package net.east301.keyring.gnome;

import com.sun.jna.Native;
import net.east301.keyring.BackendNotSupportedException;

/**
 * Global native library manager
 */
class NativeLibraryManager {

	public static synchronized void loadNativeLibraries() throws BackendNotSupportedException {
		if (glib2 != null && gklib != null)
			return;

		try {
			glib2 = (GLIB2) Native.loadLibrary("glib-2.0", GLIB2.class);
			gklib = (GKLib) Native.loadLibrary("gnome-keyring", GKLib.class);
		} catch (UnsatisfiedLinkError e) {
			throw new BackendNotSupportedException("Failed to load native library", e);
		}
	}

	/**
	 * An instance of CoreFoundationLibrary
	 */
	public static GLIB2 glib2 = null;

	/**
	 * An instance of SecurityLibrary
	 */
	public static GKLib gklib = null;

}
