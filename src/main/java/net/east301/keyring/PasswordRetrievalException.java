package net.east301.keyring;

/**
 * Represents an error while retrieving password
 */
public class PasswordRetrievalException extends Exception {

	public PasswordRetrievalException(String message) {
		super(message);
	}

	public PasswordRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}

}
