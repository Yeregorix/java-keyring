package net.east301.keyring;

public class BackendNotSupportedException extends Exception {

	public BackendNotSupportedException(String message) {
		super(message);
	}

	public BackendNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

}
