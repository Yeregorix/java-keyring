package net.east301.keyring;

/**
 * Represents an error while retrieving password
 */
public class PasswordSaveException extends Exception {

	public PasswordSaveException(String message) {
		super(message);
	}

	public PasswordSaveException(String message, Throwable cause) {
		super(message, cause);
	}

}
