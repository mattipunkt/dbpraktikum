package dev.numerouno.importer;

/**
 * Exception to be thrown when object already exists in database
 */
public class AlreadyExistsException extends RuntimeException {
    /**
     * throws exception
     * @param message custom message
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}
