package me.pbox.invoke;

import me.pbox.env.Environment;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class InvokeUtil {
    public static int run(String... args) throws InvokeException {
        try {
            ProcessBuilder pb = new ProcessBuilder(args);

            pb.directory(Environment.getPboxTempAsFile());
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

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
        try {
            ProcessBuilder pb = new ProcessBuilder(args);

            pb.directory(directory);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

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
}
