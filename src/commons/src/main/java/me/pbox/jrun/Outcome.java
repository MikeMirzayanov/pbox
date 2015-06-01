package me.pbox.jrun;

import java.util.List;

/**
 * Returning value from method run().
 *
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Outcome {
    /**
     * Process exit code or -1 on fail.
     */
    private final int exitCode;

    /**
     * Process standard output (truncated to 5MB).
     */
    private final String output;

    /**
     * Process standard error (truncated to 5MB).
     */
    private final String error;

    /**
     * Comment (may be useful in case of failure).
     */
    private final String comment;

    Outcome(int exitCode, String output, String error, List<String> errors) {
        this.exitCode = errors.isEmpty() ? exitCode : -1;
        this.output = output;
        this.error = error;

        StringBuilder errorsStringBuilder = new StringBuilder();
        for (String s : errors) {
            if (errorsStringBuilder.length() > 0) {
                errorsStringBuilder.append(", ");
            }
            errorsStringBuilder.append(s);
        }

        this.comment = errorsStringBuilder.toString();
    }

    /**
     * @return Process exit code or -1 on fail.
     */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * @return Process standard output (truncated to 5MB).
     */
    public String getOutput() {
        return output;
    }

    /**
     * @return Process standard error (truncated to 5MB).
     */
    public String getError() {
        return error;
    }

    /**
     * @return Comment (may be useful in case of failure).
     */
    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return String.format(
                "Outcome {exitCode=%d, output='%s', error='%s', comment='%s'}",
                exitCode, output, error, comment
        );
    }
}
