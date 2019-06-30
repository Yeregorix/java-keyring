package net.east301.keyring.util;

/**
 * Represents an error while lock operation
 */
public class LockException extends Exception {

    public LockException(String message) {
        super(message);
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
    }
}
