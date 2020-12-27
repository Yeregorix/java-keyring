package net.smoofyuniverse.keyring.osx;

import com.sun.jna.Pointer;
import net.smoofyuniverse.keyring.Keyring;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.UnsupportedBackendException;
import net.smoofyuniverse.keyring.util.ServiceAccountPair;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Keyring using OS X Keychain.
 */
public class OSXKeyring implements Keyring {

	public OSXKeyring() throws UnsupportedBackendException {
		try {
			Objects.requireNonNull(CoreFoundationLibrary.INSTANCE);
			Objects.requireNonNull(SecurityLibrary.INSTANCE);
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

		int status = SecurityLibrary.INSTANCE.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				dataLength, data, null);

		if (status == SecurityLibrary.ERR_SEC_ITEM_NOT_FOUND)
			return null;

		if (status != SecurityLibrary.ERR_SEC_SUCCESS)
			throw new PasswordAccessException(convertErrorCodeToMessage(status));

		byte[] passwordBytes = data[0].getByteArray(0, dataLength[0]);
		SecurityLibrary.INSTANCE.SecKeychainItemFreeContent(null, data[0]);
		return new String(passwordBytes, StandardCharsets.UTF_8);
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		ServiceAccountPair.validate(service, account);

		byte[] serviceBytes = service.getBytes(StandardCharsets.UTF_8), accountBytes = account.getBytes(StandardCharsets.UTF_8), passwordBytes = password.getBytes(StandardCharsets.UTF_8);

		Pointer[] itemRef = new Pointer[1];

		int status = SecurityLibrary.INSTANCE.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				null, null, itemRef);

		if (status != SecurityLibrary.ERR_SEC_SUCCESS && status != SecurityLibrary.ERR_SEC_ITEM_NOT_FOUND)
			throw new PasswordAccessException(convertErrorCodeToMessage(status));

		if (itemRef[0] != null) {
			status = SecurityLibrary.INSTANCE.SecKeychainItemModifyContent(
					itemRef[0], null, passwordBytes.length, passwordBytes);

			// TODO: add code to release itemRef[0]
		} else {
			status = SecurityLibrary.INSTANCE.SecKeychainAddGenericPassword(
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
		Pointer msgPtr = SecurityLibrary.INSTANCE.SecCopyErrorMessageString(errorCode, null);
		if (msgPtr == null)
			return null;

		int bufSize = (int) CoreFoundationLibrary.INSTANCE.CFStringGetLength(msgPtr);
		char[] buf = new char[bufSize];

		for (int i = 0; i < buf.length; i++)
			buf[i] = CoreFoundationLibrary.INSTANCE.CFStringGetCharacterAtIndex(msgPtr, i);

		CoreFoundationLibrary.INSTANCE.CFRelease(msgPtr);
		return new String(buf);
	}
}