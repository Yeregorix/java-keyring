package net.smoofyuniverse.keyring;

/**
 * Represents an error while accessing a password.
 */
public class PasswordAccessException extends Exception {

	public PasswordAccessException(String message) {
		super(message);
	}

	public PasswordAccessException(String message, Throwable cause) {
		super(message, cause);
	}

}
