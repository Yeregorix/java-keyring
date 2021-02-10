package net.smoofyuniverse.keyring.mac;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import net.smoofyuniverse.keyring.util.NativeUtil;

/**
 * MacOS CoreFoundation library.
 */
public interface CoreFoundationLib extends Library {
	CoreFoundationLib INSTANCE = NativeUtil.loadOrNull("CoreFoundation", CoreFoundationLib.class);

	long CFStringGetLength(              // CFIndex
										 Pointer theString);                 // CFStringRef

	char CFStringGetCharacterAtIndex(    // UniChar
										 Pointer theString,                  // CFStringRef
										 long idx);                          // CFIndex

	void CFRelease(                      // void
										 Pointer cf);                        // CFTypeRef
}
