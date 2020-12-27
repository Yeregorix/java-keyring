package net.smoofyuniverse.keyring;

import java.nio.file.Path;

public abstract class KeyringBackend {

	protected Path keyStorePath;

	/**
	 * Setup actual key store
	 */
	public void setup() throws BackendNotSupportedException {}

	/**
	 * Gets path to key store
	 */
	public Path getKeyStorePath() {
		return this.keyStorePath;
	}

	/**
	 * Sets path to key store
	 *
	 * @param path Path to key store
	 */
	public void setKeyStorePath(Path path) {
		this.keyStorePath = path;
	}

	/**
	 * Returns true when the backend is supported
	 */
	public abstract boolean isSupported();

	/**
	 * Returns true if the backend directory uses some file to store passwords
	 */
	public abstract boolean isKeyStorePathRequired();

	/**
	 * Gets password from key store
	 *
	 * @param service Service name
	 * @param account Account name
	 * @return Password related to specified service and account
	 * @throws PasswordAccessException Thrown when an error happened while getting the password
	 */
	public abstract String getPassword(String service, String account) throws PasswordAccessException;

	/**
	 * Sets password to key store
	 *
	 * @param service  Service name
	 * @param account  Account name
	 * @param password Password
	 * @throws PasswordAccessException Thrown when an error happened while saving the password
	 */
	public abstract void setPassword(String service, String account, String password) throws PasswordAccessException;

	/**
	 * Gets backend ID
	 */
	public abstract String getID();

}
