package net.smoofyuniverse.keyring.mac;

import com.sun.jna.Pointer;
import net.smoofyuniverse.keyring.Keyring;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.UnsupportedBackendException;

import java.nio.charset.StandardCharsets;

/**
 * A keyring using MacOS Keychain.
 * See https://developer.apple.com/documentation/security/keychain_services/keychain_items
 */
public class KeychainKeyring implements Keyring {

	/**
	 * Creates a new keyring using MacOS Keychain.
	 *
	 * @throws UnsupportedBackendException if the backend for this implementation is not available.
	 */
	public KeychainKeyring() throws UnsupportedBackendException {
		if (CoreFoundationLib.INSTANCE == null || SecurityLib.INSTANCE == null)
			throw new UnsupportedBackendException("Failed to load native libraries");
	}

	@Override
	public String getBackendName() {
		return "MacOS Keychain";
	}

	@Override
	public String getPassword(String service, String account) throws PasswordAccessException {
		Keyring.validateService(service);
		Keyring.validateAccount(account);

		byte[] serviceBytes = service.getBytes(StandardCharsets.UTF_8),
				accountBytes = account.getBytes(StandardCharsets.UTF_8);

		int[] dataLength = new int[1];
		Pointer[] data = new Pointer[1];

		int status = SecurityLib.INSTANCE.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				dataLength, data, null); // find password

		if (status == SecurityLib.ERR_SEC_ITEM_NOT_FOUND)
			return null;

		if (status != 0)
			throw new PasswordAccessException(errorCodeToMessage(status));

		byte[] passwordBytes = data[0].getByteArray(0, dataLength[0]);
		SecurityLib.INSTANCE.SecKeychainItemFreeContent(null, data[0]);
		return new String(passwordBytes, StandardCharsets.UTF_8);
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		Keyring.validateService(service);
		Keyring.validateAccount(account);
		Keyring.validatePassword(password);

		byte[] serviceBytes = service.getBytes(StandardCharsets.UTF_8),
				accountBytes = account.getBytes(StandardCharsets.UTF_8);

		Pointer[] itemRef = new Pointer[1];

		int status = SecurityLib.INSTANCE.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				null, null, itemRef); // find item

		if (status != 0 && status != SecurityLib.ERR_SEC_ITEM_NOT_FOUND)
			throw new PasswordAccessException(errorCodeToMessage(status));

		if (itemRef[0] != null) {
			try {
				if (password == null) { // delete
					status = SecurityLib.INSTANCE.SecKeychainItemDelete(itemRef[0]);
				} else { // modify
					byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
					status = SecurityLib.INSTANCE.SecKeychainItemModifyContent(
							itemRef[0], null, passwordBytes.length, passwordBytes);
				}
			} finally {
				CoreFoundationLib.INSTANCE.CFRelease(itemRef[0]);
			}
		} else {
			if (password == null) { // skip
				status = 0;
			} else { // set
				byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
				status = SecurityLib.INSTANCE.SecKeychainAddGenericPassword(
						null, serviceBytes.length, serviceBytes,
						accountBytes.length, accountBytes,
						passwordBytes.length, passwordBytes, null);
			}
		}

		if (status != 0)
			throw new PasswordAccessException(errorCodeToMessage(status));
	}

	/**
	 * Converts OSStat to error message
	 *
	 * @param errorCode OSStat to be converted
	 */
	private String errorCodeToMessage(int errorCode) {
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
