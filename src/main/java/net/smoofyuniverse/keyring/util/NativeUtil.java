package net.smoofyuniverse.keyring.util;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class NativeUtil {

	public static <T extends Library> T loadOrNull(String name, Class<T> interfaceClass) {
		try {
			return Native.load(name, interfaceClass);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
}
