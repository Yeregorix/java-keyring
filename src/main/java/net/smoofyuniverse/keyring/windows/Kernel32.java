package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Library;
import net.smoofyuniverse.keyring.util.NativeUtil;

public interface Kernel32 extends Library {
	Kernel32 INSTANCE = NativeUtil.loadOrNull("Kernel32", Kernel32.class);

	/**
	 * Gets the last error.
	 *
	 * @return The integer representing the last error.
	 */
	Integer GetLastError();

}
