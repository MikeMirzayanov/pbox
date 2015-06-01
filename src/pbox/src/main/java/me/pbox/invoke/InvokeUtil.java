package me.pbox.invoke;

import me.pbox.env.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class InvokeUtil {
    private static final Logger logger = Logger.getLogger(InvokeUtil.class);

    public static int run(boolean inheritOutput, boolean inheritError, String... args) throws InvokeException {
        try {
            ProcessBuilder pb = new ProcessBuilder(args);

            pb.directory(Environment.getPboxTempAsFile());

            if (inheritOutput) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }

            if (inheritError) {
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }

            logger.info("Starting '" + StringUtils.join(args, " ") + "'.");
            Process process = pb.start();
            try {
                return process.waitFor();
            } finally {
                process.destroy();
            }
        } catch (IOException | InterruptedException e) {
            throw new InvokeException("Can't run " + StringUtils.join(args, ' ') + ".", e);
        }
    }

    public static int run(String... args) throws InvokeException {
        return run(true, true, null, args);
    }

    public static int run(boolean inheritOutput, boolean inheritError, File directory, String... args) throws InvokeException {
        try {
            ProcessBuilder pb = new ProcessBuilder(args);

            pb.directory(directory);

            if (inheritOutput) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }

            if (inheritError) {
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }

            logger.info("Starting '" + StringUtils.join(args, " ") + "'.");
            Process process = pb.start();
            try {
                return process.waitFor();
            } finally {
                process.destroy();
            }
        } catch (IOException | InterruptedException e) {
            throw new InvokeException("Can't run " + StringUtils.join(args, ' ') + ".", e);
        }
    }

    public static int run(File directory, String... args) throws InvokeException {
        return run(true, true, directory, args);
    }
}
