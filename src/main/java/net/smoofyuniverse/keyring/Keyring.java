package net.smoofyuniverse.keyring;

import com.sun.jna.Platform;
import net.smoofyuniverse.keyring.linux.SecretServiceKeyring;
import net.smoofyuniverse.keyring.osx.OSXKeyring;
import net.smoofyuniverse.keyring.windows.WinCredentialKeyring;

/**
 * A keyring.
 */
public interface Keyring {

	/**
	 * Creates a keyring for the current OS.
	 *
	 * @return The keyring.
	 * @throws UnsupportedBackendException if there is no backend available.
	 */
	static Keyring create() throws UnsupportedBackendException {
		switch (Platform.getOSType()) {
			case Platform.MAC:
				return new OSXKeyring();
			case Platform.LINUX:
				return new SecretServiceKeyring();
			case Platform.WINDOWS:
			case Platform.WINDOWSCE:
				return new WinCredentialKeyring();
			default:
				throw new UnsupportedBackendException("Unsupported OS");
		}
	}

	/**
	 * Gets the name of the backend.
	 *
	 * @return The name.
	 */
	String getBackendName();

	/**
	 * Gets a password for the specified service and account.
	 *
	 * @param service The service.
	 * @param account The account.
	 * @return The password or null if absent.
	 * @throws PasswordAccessException if any exception occurs while getting the password.
	 */
	String getPassword(String service, String account) throws PasswordAccessException;

	/**
	 * Sets a password for the specified service and account.
	 *
	 * @param service  The service.
	 * @param account  The account.
	 * @param password The password or null to remove.
	 * @throws PasswordAccessException if any exception occurs while saving the password.
	 */
	void setPassword(String service, String account, String password) throws PasswordAccessException;
}
