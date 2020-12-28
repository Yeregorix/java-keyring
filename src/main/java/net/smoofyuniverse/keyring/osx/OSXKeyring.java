package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Pointer;
import net.smoofyuniverse.keyring.Keyring;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.UnsupportedBackendException;
import net.smoofyuniverse.keyring.util.ServiceAccountPair;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A keyring using OS X Keychain.
 */
public class OSXKeyring implements Keyring {

	/**
	 * Creates a new keyring using OS X Keychain.
	 *
	 * @throws UnsupportedBackendException if the backend for this implementation is not available.
	 */
	public OSXKeyring() throws UnsupportedBackendException {
		try {
			Objects.requireNonNull(CoreFoundationLib.INSTANCE);
			Objects.requireNonNull(SecurityLib.INSTANCE);
		} catch (Throwable t) {
			throw new UnsupportedBackendException("Failed to load native libraries", t);
		}
	}

	@Override
	public String getBackendName() {
		return "OSXKeychain";
	}

	@Override
	public String getPassword(String service, String account) throws PasswordAccessException {
		ServiceAccountPair.validate(service, account);

		byte[] serviceBytes = service.getBytes(StandardCharsets.UTF_8), accountBytes = account.getBytes(StandardCharsets.UTF_8);

		int[] dataLength = new int[1];
		Pointer[] data = new Pointer[1];

		int status = SecurityLib.INSTANCE.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				dataLength, data, null);

		if (status == SecurityLib.ERR_SEC_ITEM_NOT_FOUND)
			return null;

		if (status != SecurityLib.ERR_SEC_SUCCESS)
			throw new PasswordAccessException(convertErrorCodeToMessage(status));

		byte[] passwordBytes = data[0].getByteArray(0, dataLength[0]);
		SecurityLib.INSTANCE.SecKeychainItemFreeContent(null, data[0]);
		return new String(passwordBytes, StandardCharsets.UTF_8);
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		ServiceAccountPair.validate(service, account);

		byte[] serviceBytes = service.getBytes(StandardCharsets.UTF_8),
				accountBytes = account.getBytes(StandardCharsets.UTF_8),
				passwordBytes = password.getBytes(StandardCharsets.UTF_8);

		Pointer[] itemRef = new Pointer[1];

		int status = SecurityLib.INSTANCE.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				null, null, itemRef);

		if (status != SecurityLib.ERR_SEC_SUCCESS && status != SecurityLib.ERR_SEC_ITEM_NOT_FOUND)
			throw new PasswordAccessException(convertErrorCodeToMessage(status));

		if (itemRef[0] != null) {
			try {
				status = SecurityLib.INSTANCE.SecKeychainItemModifyContent(
						itemRef[0], null, passwordBytes.length, passwordBytes);
			} finally {
				CoreFoundationLib.INSTANCE.CFRelease(itemRef[0]);
			}
		} else {
			status = SecurityLib.INSTANCE.SecKeychainAddGenericPassword(
					Pointer.NULL, serviceBytes.length, serviceBytes,
					accountBytes.length, accountBytes,
					passwordBytes.length, passwordBytes, null);
		}

		if (status != 0)
			throw new PasswordAccessException(convertErrorCodeToMessage(status));
	}

	/**
	 * Converts OSStat to error message
	 *
	 * @param errorCode OSStat to be converted
	 */
	private String convertErrorCodeToMessage(int errorCode) {
		Pointer msgPtr = SecurityLib.INSTANCE.SecCopyErrorMessageString(errorCode, null);
		if (msgPtr == null)
			return null;

		int bufSize = (int) CoreFoundationLib.INSTANCE.CFStringGetLength(msgPtr);
		char[] buf = new char[bufSize];

		for (int i = 0; i < buf.length; i++)
			buf[i] = CoreFoundationLib.INSTANCE.CFStringGetCharacterAtIndex(msgPtr, i);

		CoreFoundationLib.INSTANCE.CFRelease(msgPtr);
		return new String(buf);
	}
}
