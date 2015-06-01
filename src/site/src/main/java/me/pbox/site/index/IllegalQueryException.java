package me.pbox.site.index;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class IllegalQueryException extends Exception {
    public IllegalQueryException(String message) {
        super(message);
    }

    public IllegalQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
