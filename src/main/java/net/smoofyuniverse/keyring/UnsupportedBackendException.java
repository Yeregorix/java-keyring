package net.smoofyuniverse.keyring;

/**
 * Indicates that the backend is not supported in the current runtime.
 */
public class UnsupportedBackendException extends Exception {

	public UnsupportedBackendException(String message) {
		super(message);
	}

	public UnsupportedBackendException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedBackendException(Throwable cause) {
		super(cause);
	}

}
