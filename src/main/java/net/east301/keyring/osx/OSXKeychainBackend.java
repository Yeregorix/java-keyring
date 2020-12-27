package net.east301.keyring.osx;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import net.east301.keyring.BackendNotSupportedException;
import net.east301.keyring.KeyringBackend;
import net.east301.keyring.PasswordRetrievalException;
import net.east301.keyring.PasswordSaveException;
import net.east301.keyring.util.ServiceAndAccount;

import java.nio.charset.StandardCharsets;

/**
 * Keyring backend which uses OS X Keychain
 */
public class OSXKeychainBackend extends KeyringBackend {

	@Override
	public String getID() {
		return "OSXKeychain";
	}

	@Override
	public void setup() throws BackendNotSupportedException {
		NativeLibraryManager.loadNativeLibraries();
	}

	@Override
	public boolean isSupported() {
		return Platform.isMac();
	}

	@Override
	public boolean isKeyStorePathRequired() {
		return false;
	}

	@Override
	public String getPassword(String service, String account) throws PasswordRetrievalException {
		ServiceAndAccount.validate(service, account);

		byte[] serviceBytes = service.getBytes(StandardCharsets.UTF_8), accountBytes = account.getBytes(StandardCharsets.UTF_8);

		int[] dataLength = new int[1];
		Pointer[] data = new Pointer[1];

		int status = NativeLibraryManager.Security.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				dataLength, data, null);

		if (status == SecurityLibrary.ERR_SEC_ITEM_NOT_FOUND)
			return null;

		if (status != SecurityLibrary.ERR_SEC_SUCCESS)
			throw new PasswordRetrievalException(convertErrorCodeToMessage(status));

		byte[] passwordBytes = data[0].getByteArray(0, dataLength[0]);
		NativeLibraryManager.Security.SecKeychainItemFreeContent(null, data[0]);
		return new String(passwordBytes, StandardCharsets.UTF_8);
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordSaveException {
		ServiceAndAccount.validate(service, account);

		byte[] serviceBytes = service.getBytes(StandardCharsets.UTF_8), accountBytes = account.getBytes(StandardCharsets.UTF_8), passwordBytes = password.getBytes(StandardCharsets.UTF_8);

		Pointer[] itemRef = new Pointer[1];

		int status = NativeLibraryManager.Security.SecKeychainFindGenericPassword(
				null, serviceBytes.length, serviceBytes,
				accountBytes.length, accountBytes,
				null, null, itemRef);

		if (status != SecurityLibrary.ERR_SEC_SUCCESS && status != SecurityLibrary.ERR_SEC_ITEM_NOT_FOUND)
			throw new PasswordSaveException(convertErrorCodeToMessage(status));

		if (itemRef[0] != null) {
			status = NativeLibraryManager.Security.SecKeychainItemModifyContent(
					itemRef[0], null, passwordBytes.length, passwordBytes);

			// TODO: add code to release itemRef[0]
		} else {
			status = NativeLibraryManager.Security.SecKeychainAddGenericPassword(
					Pointer.NULL, serviceBytes.length, serviceBytes,
					accountBytes.length, accountBytes,
					passwordBytes.length, passwordBytes, null);
		}

		if (status != 0)
			throw new PasswordSaveException(convertErrorCodeToMessage(status));
	}

	/**
	 * Converts OSStat to error message
	 *
	 * @param errorCode OSStat to be converted
	 */
	private String convertErrorCodeToMessage(int errorCode) {
		Pointer msgPtr = NativeLibraryManager.Security.SecCopyErrorMessageString(errorCode, null);
		if (msgPtr == null)
			return null;

		int bufSize = (int) NativeLibraryManager.CoreFoundation.CFStringGetLength(msgPtr);
		char[] buf = new char[bufSize];

		for (int i = 0; i < buf.length; i++)
			buf[i] = NativeLibraryManager.CoreFoundation.CFStringGetCharacterAtIndex(msgPtr, i);

		NativeLibraryManager.CoreFoundation.CFRelease(msgPtr);
		return new String(buf);
	}

}
