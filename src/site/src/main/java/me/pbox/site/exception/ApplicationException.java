package me.pbox.site.exception;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
