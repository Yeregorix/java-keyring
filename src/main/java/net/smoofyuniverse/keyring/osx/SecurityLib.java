package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import net.smoofyuniverse.keyring.util.NativeUtil;

/**
 * OS X Security library.
 */
public interface SecurityLib extends Library {
	SecurityLib INSTANCE = NativeUtil.loadOrNull("Security", SecurityLib.class);

	int ERR_SEC_ITEM_NOT_FOUND = -25300;

	int SecKeychainFindGenericPassword(  // OSStatus
										 Pointer keychainOrArray,            // CFTypeRef
										 int serviceNameLength,              // UInt32
										 byte[] serviceName,                 // const char*
										 int accountNameLength,              // UInt32
										 byte[] accountName,                 // const char*
										 int[] passwordLength,               // UInt32*
										 Pointer[] passwordData,             // void**
										 Pointer[] itemRef);                 // SecKeychaingItemRef*

	int SecKeychainAddGenericPassword(   // OSStatus
										 Pointer keychain,                   // SecKeychainRef
										 int serviceNameLength,              // UInt32
										 byte[] serviceName,                 // const char*
										 int accountNameLength,              // UInt32
										 byte[] accountName,                 // const char*
										 int passwordLength,                 // UInt32
										 byte[] passwordData,                // const void*
										 Pointer itemRef);                   // SecKeychainItemRef

	int SecKeychainItemModifyContent(    // OSStatus
										 Pointer itemRef,                    // SecKeychainItemRef
										 Pointer attrList,                   // const SecKeychainAttributeList*
										 int length,                         // UInt32
										 byte[] data);                       // const void*

	int SecKeychainItemDelete(           // OSStatus
										 Pointer itemRef);                   // SecKeychainItemRef

	Pointer SecCopyErrorMessageString(   // CFStringRef
										 int status,                         // OSStatus
										 Pointer reserved);                  // void*

	int SecKeychainItemFreeContent(      // OSStatus
										 Pointer[] attrList,                 // SecKeychainAttributeList*
										 Pointer data);                      // void*
}
