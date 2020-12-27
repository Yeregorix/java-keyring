package net.smoofyuniverse.keyring;

import java.nio.file.Path;

/**
 * Keyring
 */
public class Keyring {

	/**
	 * Keyring backend
	 */
	private final KeyringBackend backend;

	/**
	 * Creates an instance of Keyring
	 */
	public static Keyring create() throws BackendNotSupportedException {
		return new Keyring(KeyringBackendFactory.create());
	}

	/**
	 * Creates an instance of Keyring with specified backend
	 *
	 * @param backendType Backend type
	 */
	public static Keyring create(String backendType) throws BackendNotSupportedException {
		return new Keyring(KeyringBackendFactory.create(backendType));
	}

	/**
	 * Initializes an instance of Keyring
	 *
	 * @param backend Keyring backend instance
	 */
	private Keyring(KeyringBackend backend) {
		this.backend = backend;
	}

	/**
	 * Returns keyring backend instance
	 */
	public KeyringBackend getBackend() {
		return this.backend;
	}

	/**
	 * Gets path to key store
	 * (Proxy method of KeyringBackend.getKeyStorePath)
	 */
	public Path getKeyStorePath() {
		return this.backend.getKeyStorePath();
	}

	/**
	 * Sets path to key store
	 * (Proxy method of KeyringBackend.setKeyStorePath)
	 *
	 * @param path Path to key store
	 */
	public void setKeyStorePath(Path path) {
		this.backend.setKeyStorePath(path);
	}

	/**
	 * Returns true if the backend directory uses some file to store passwords
	 * (Proxy method of KeyringBackend.isKeyStorePathRequired)
	 */
	public boolean isKeyStorePathRequired() {
		return this.backend.isKeyStorePathRequired();
	}

	/**
	 * Gets password from key store
	 * (Proxy method of KeyringBackend.getPassword)
	 *
	 * @param service Service name
	 * @param account Account name
	 * @return Password related to specified service and account
	 * @throws PasswordAccessException Thrown when an error happened while getting the password
	 */
	public String getPassword(String service, String account) throws PasswordAccessException {
		return this.backend.getPassword(service, account);
	}

	/**
	 * Sets password to key store
	 * (Proxy method of KeyringBackend.setPassword)
	 *
	 * @param service  Service name
	 * @param account  Account name
	 * @param password Password
	 * @throws PasswordAccessException Thrown when an error happened while saving the password
	 */
	public void setPassword(String service, String account, String password) throws PasswordAccessException {
		this.backend.setPassword(service, account, password);
	}

}
