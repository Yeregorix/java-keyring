package net.smoofyuniverse.keyring;

import com.sun.jna.Platform;
import net.smoofyuniverse.keyring.linux.SecretServiceKeyring;
import net.smoofyuniverse.keyring.mac.KeychainKeyring;
import net.smoofyuniverse.keyring.windows.CredentialManagerKeyring;

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
				return new KeychainKeyring();
			case Platform.LINUX:
				return new SecretServiceKeyring();
			case Platform.WINDOWS:
			case Platform.WINDOWSCE:
				return new CredentialManagerKeyring();
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

	/**
	 * Validates the service.
	 * The service must be non-null and not empty.
	 *
	 * @param service The service.
	 */
	static void validateService(String service) {
		if (service == null)
			throw new IllegalArgumentException("service is null");
		if (service.isEmpty())
			throw new IllegalArgumentException("service is empty");
		if (service.indexOf('\0') != -1)
			throw new IllegalArgumentException("service contains a null character");
	}

	/**
	 * Validates the account.
	 * The account must be non-null.
	 *
	 * @param account The account.
	 */
	static void validateAccount(String account) {
		if (account == null)
			throw new IllegalArgumentException("account is null");
		if (account.indexOf('\0') != -1)
			throw new IllegalArgumentException("account contains a null character");
	}

	/**
	 * Validates the password.
	 * The password must not contains a NULL character.
	 *
	 * @param password The password.
	 */
	static void validatePassword(String password) {
		if (password != null && password.indexOf('\0') != -1)
			throw new IllegalArgumentException("password contains a null character");
	}
}
