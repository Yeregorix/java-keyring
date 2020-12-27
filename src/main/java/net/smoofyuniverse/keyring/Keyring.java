package net.smoofyuniverse.keyring;

import com.sun.jna.Platform;
import net.smoofyuniverse.keyring.gnome.GNOMEKeyring;
import net.smoofyuniverse.keyring.osx.OSXKeyring;
import net.smoofyuniverse.keyring.windows.WindowsDPAPIKeyring;

import java.nio.file.Path;

/**
 * A keyring.
 */
public interface Keyring {

	/**
	 * Creates a keyring for the current OS.
	 *
	 * @param keyStore The key store.
	 */
	static Keyring create(Path keyStore) throws UnsupportedBackendException {
		switch (Platform.getOSType()) {
			case Platform.MAC:
				return new OSXKeyring();
			case Platform.LINUX:
				return new GNOMEKeyring(keyStore);
			case Platform.WINDOWS:
			case Platform.WINDOWSCE:
				return new WindowsDPAPIKeyring(keyStore);
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
	 * @return The password.
	 * @throws PasswordAccessException if any exception occurs while getting the password.
	 */
	String getPassword(String service, String account) throws PasswordAccessException;

	/**
	 * Sets a password for the specified service and account.
	 *
	 * @param service  The service.
	 * @param account  The account.
	 * @param password The password.
	 * @throws PasswordAccessException if any exception occurs while saving the password.
	 */
	void setPassword(String service, String account, String password) throws PasswordAccessException;
}
