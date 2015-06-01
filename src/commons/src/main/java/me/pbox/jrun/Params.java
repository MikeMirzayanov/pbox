package me.pbox.jrun;

import java.io.File;

/**
 * Process parameters:
 * - invocation directory,
 * - time-limit (is milliseconds, 0 stands for infinite).
 * - redirect input file,
 * - redirect output file,
 * - redirect error file.
 *
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
@SuppressWarnings("UnusedDeclaration")
public class Params {
    /**
     * Invocation directory.
     */
    private final File directory;

    /**
     * Time limit in milliseconds (0 stands for infinite).
     */
    private final long timeLimit;

    /**
     * File to be redirected to process standard input or {@code null} iff no input redirection used.
     */
    private final File redirectInputFile;

    /**
     * File to redirect a process standard output or {@code null} iff no output redirection used.
     */
    private final File redirectOutputFile;

    /**
     * File to redirect a process standard error or {@code null} iff no error redirection used.
     */
    private final File redirectErrorFile;

    /**
     * @param directory          Invocation directory.
     * @param timeLimit          Time limit in milliseconds (0 stands for infinite).
     * @param redirectInputFile  Input redirection file or {@code null}.
     * @param redirectOutputFile Output redirection file or {@code null}.
     * @param redirectErrorFile  Error redirection file or {@code null}.
     */
    private Params(File directory, long timeLimit, File redirectInputFile, File redirectOutputFile, File redirectErrorFile) {
        this.directory = directory;
        this.timeLimit = timeLimit;
        this.redirectInputFile = redirectInputFile;
        this.redirectOutputFile = redirectOutputFile;
        this.redirectErrorFile = redirectErrorFile;
    }

    /**
     * @return Invocation directory.
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * @return Time limit in milliseconds (0 stands for infinite).
     */
    public long getTimeLimit() {
        return timeLimit;
    }

    /**
     * @return File to be redirected to process standard input or {@code null} iff no input redirection used.
     */
    public File getRedirectInputFile() {
        return redirectInputFile;
    }

    /**
     * @return File to redirect a process standard output or {@code null} iff no output redirection used.
     */
    public File getRedirectOutputFile() {
        return redirectOutputFile;
    }

    /**
     * @return File to redirect a process standard error or {@code null} iff no error redirection used.
     */
    public File getRedirectErrorFile() {
        return redirectErrorFile;
    }

    /**
     * Immutable class Params builder.
     */
    public static final class Builder {
        private File directory;
        private long timeLimit;
        private File redirectInputFile;
        private File redirectOutputFile;
        private File redirectErrorFile;

        /**
         * @param directory Invocation directory.
         * @return this.
         */
        public Builder setDirectory(File directory) {
            this.directory = directory;
            return this;
        }

        /**
         * @param timeLimit Time limit in milliseconds (0 stands for infinite).
         * @return this
         */
        public Builder setTimeLimit(long timeLimit) {
            this.timeLimit = timeLimit;
            return this;
        }

        /**
         * @param redirectInputFile File to be redirected to process standard input or {@code null} iff no input redirection used.
         * @return this
         */
        public Builder setRedirectInputFile(File redirectInputFile) {
            this.redirectInputFile = redirectInputFile;
            return this;
        }

        /**
         * @param redirectOutputFile File to redirect a process standard output or {@code null} iff no output redirection used.
         * @return this
         */
        public Builder setRedirectOutputFile(File redirectOutputFile) {
            this.redirectOutputFile = redirectOutputFile;
            return this;
        }

        /**
         * @param redirectErrorFile File to redirect a process standard error or {@code null} iff no error redirection used.
         * @return this
         */
        public Builder setRedirectErrorFile(File redirectErrorFile) {
            this.redirectErrorFile = redirectErrorFile;
            return this;
        }

        /**
         * @return New Params instance by builder.
         */
        public Params newInstance() {
            return new Params(directory, timeLimit, redirectInputFile, redirectOutputFile, redirectErrorFile);
        }
    }
}
