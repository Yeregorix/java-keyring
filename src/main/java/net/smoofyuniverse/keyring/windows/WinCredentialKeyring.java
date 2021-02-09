package net.smoofyuniverse.keyring.windows;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.PointerByReference;
import net.smoofyuniverse.keyring.Keyring;
import net.smoofyuniverse.keyring.PasswordAccessException;
import net.smoofyuniverse.keyring.UnsupportedBackendException;

import java.nio.charset.StandardCharsets;

/**
 * A keyring using Windows Credential Manager.
 * See https://docs.microsoft.com/fr-fr/windows/win32/api/wincred
 */
public class WinCredentialKeyring implements Keyring {

	/**
	 * Creates a new keyring using Windows Credential Manager.
	 *
	 * @throws UnsupportedBackendException if the backend for this implementation is not available.
	 */
	public WinCredentialKeyring() throws UnsupportedBackendException {
		if (Advapi32.INSTANCE == null || Kernel32.INSTANCE == null)
			throw new UnsupportedBackendException("Failed to load native libraries");
		// if we pass this check, then Crypt32.INSTANCE should be available
	}

	@Override
	public String getBackendName() {
		return "Windows Credential Manager";
	}

	protected String getTargetName(String service, String account) {
		if (account.isEmpty())
			return service;

		// https://docs.microsoft.com/en-us/windows/win32/secauthn/user-name-formats
		return service + "\\" + account;
	}

	@Override
	public String getPassword(String service, String account) throws PasswordAccessException {
		Keyring.validateService(service);
		Keyring.validateAccount(account);

		PointerByReference ref = new PointerByReference();
		boolean success = Advapi32.INSTANCE.CredReadA(getTargetName(service, account), new DWORD(1L), new DWORD(0L), ref);
		if (!success) {
			int error = Kernel32.INSTANCE.GetLastError();
			if (error == Advapi32.ERROR_NOT_FOUND)
				return null;

			throw new PasswordAccessException(errorCodeToMessage(error));
		}

		CREDENTIAL cred = new CREDENTIAL(ref.getValue());
		try {
			byte[] passwordBytes = cred.CredentialBlob.getByteArray(0, cred.CredentialBlobSize);
			return new String(passwordBytes, StandardCharsets.UTF_16LE);
		} catch (Exception e) {
			throw new PasswordAccessException(e.getMessage());
		} finally {
			Advapi32.INSTANCE.CredFree(ref.getValue());
		}
	}

	@Override
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		Keyring.validateService(service);
		Keyring.validateAccount(account);
		Keyring.validatePassword(password);

		String target = getTargetName(service, account);
		boolean success;

		if (password == null) {
			success = Advapi32.INSTANCE.CredDeleteA(target, new DWORD(1), new DWORD(0));
		} else {
			byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_16LE);
			Memory passwordMemory = new Memory(passwordBytes.length);
			passwordMemory.write(0, passwordBytes, 0, passwordBytes.length);

			CREDENTIAL cred = new CREDENTIAL();
			cred.TargetName = target;
			cred.UserName = account;
			cred.Type = 1;
			cred.CredentialBlob = passwordMemory;
			cred.CredentialBlobSize = passwordBytes.length;
			cred.Persist = 2;

			success = Advapi32.INSTANCE.CredWriteA(cred, new DWORD(0));
			passwordMemory.clear();
		}

		if (!success) {
			int error = Kernel32.INSTANCE.GetLastError();
			if (error != Advapi32.ERROR_NOT_FOUND)
				throw new PasswordAccessException(errorCodeToMessage(error));
		}
	}

	private String errorCodeToMessage(int errorCode) {
		return "Error code: " + errorCode; // TODO get message from Windows API
	}
}
