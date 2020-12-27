package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * OS X CoreFoundation library
 */
interface CoreFoundationLibrary extends Library {

	long CFStringGetLength(              // CFIndex
										 Pointer theString);                 // CFStringRef

	char CFStringGetCharacterAtIndex(    // UniChar
										 Pointer theString,                  // CFStringRef
										 long idx);                          // CFIndex

	void CFRelease(                      // void
										 Pointer cf);                        // CFTypeRef

}
