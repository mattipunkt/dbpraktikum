package dev.numerouno.importer;

/**
 * Thrown to indicate that a data integrity violation has occurred during an import operation.
 *
 * <p>This exception is typically used to signal that imported data failed to meet
 * certain consistency or integrity constraints.</p>
 *
 * <p>It is a subclass of {@link RuntimeException}, so it is an unchecked exception.</p>
 *
 * @see RuntimeException
 */
public class IntegrityException extends RuntimeException {

    /**
     * Constructs a new {@code IntegrityException} with the specified detail message.
     *
     * @param message the detail message describing the integrity violation
     */
    public IntegrityException(String message) {
        super(message);
    }
}
