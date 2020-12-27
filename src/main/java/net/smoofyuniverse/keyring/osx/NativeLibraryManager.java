package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Native;
import net.smoofyuniverse.keyring.BackendNotSupportedException;

/**
 * Global native library manager
 */
class NativeLibraryManager {

	public static synchronized void loadNativeLibraries() throws BackendNotSupportedException {
		if (CoreFoundation != null && Security != null)
			return;

		try {
			CoreFoundation = (CoreFoundationLibrary) Native.loadLibrary("CoreFoundation", CoreFoundationLibrary.class);
			Security = (SecurityLibrary) Native.loadLibrary("Security", SecurityLibrary.class);
		} catch (UnsatisfiedLinkError e) {
			throw new BackendNotSupportedException("Failed to load native library", e);
		}
	}

	/**
	 * An instance of CoreFoundationLibrary
	 */
	public static CoreFoundationLibrary CoreFoundation = null;

	/**
	 * An instance of SecurityLibrary
	 */
	public static SecurityLibrary Security = null;

}
