package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * OS X CoreFoundation library
 */
public interface CoreFoundationLib extends Library {
	CoreFoundationLib INSTANCE = Native.load("CoreFoundation", CoreFoundationLib.class);

	long CFStringGetLength(              // CFIndex
										 Pointer theString);                 // CFStringRef

	char CFStringGetCharacterAtIndex(    // UniChar
										 Pointer theString,                  // CFStringRef
										 long idx);                          // CFIndex

	void CFRelease(                      // void
										 Pointer cf);                        // CFTypeRef
}
