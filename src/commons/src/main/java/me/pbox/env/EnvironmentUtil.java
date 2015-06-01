package me.pbox.env;

import me.pbox.invoke.InvokeException;
import me.pbox.invoke.InvokeUtil;
import org.apache.log4j.Logger;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class EnvironmentUtil {
    private static final Logger logger = Logger.getLogger(EnvironmentUtil.class);

    public static boolean appendPath(String directory) {
        logger.info("Append to the PATH the directory `" + directory + "`.");

        try {
            boolean result = InvokeUtil.run(Environment.getBin("pathed"), "-a", directory, "-s") == 0;
            if (result) {
                logger.info("Successfully appended to the PATH the directory `" + directory + "`.");
            } else {
                logger.warn("Failed to append to the PATH the directory `" + directory + "`.");
            }
            return result;
        } catch (InvokeException e) {
            logger.warn("Can't append to PATH `" + directory + "`.", e);
            return false;
        }
    }

    public static boolean removePath(String directory) {
        logger.info("Remove from the PATH the directory `" + directory + "`.");

        try {
            boolean result = InvokeUtil.run(Environment.getBin("pathed"), "-r", directory, "-s") == 0;
            if (result) {
                logger.info("Successfully removed from the PATH the directory `" + directory + "`.");
            } else {
                logger.warn("Failed to remove from the PATH the directory `" + directory + "`.");
            }
            return result;
        } catch (InvokeException e) {
            logger.warn("Can't remove from PATH `" + directory + "`.", e);
            return false;
        }
    }

    public static boolean setEnvironmentVariable(String name, String value) {
        logger.info("Set environment variable `" + name + "=" + value + "`.");

        try {
            boolean result = InvokeUtil.run("setx.exe", name, value, "/M") == 0;
            if (result) {
                logger.info("Successfully set environment variable `" + name + "=" + value + "`.");
            } else {
                logger.warn("Failed to set environment variable `" + name + "=" + value + "`.");
            }
            return result;
        } catch (InvokeException e) {
            logger.warn("Can't set environment variable `" + name + "` to value `" + value + "`.", e);
            return false;
        }
    }

    public static boolean removeEnvironmentVariable(String name) {
        logger.info("Remove environment variable `" + name + "`.");

        try {
            int setxExitCode = InvokeUtil.run("setx.exe", name, "\"\"", "/M");

            try {
                boolean result = InvokeUtil.run("reg.exe", "delete", "HKLM\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment", "/F", "/V", name) == 0
                        && setxExitCode == 0;
                if (result) {
                    logger.info("Successfully removed environment variable `" + name + "`.");
                } else {
                    logger.warn("Failed to remove environment variable `" + name + "`.");
                }
                return result;
            } catch (InvokeException e) {
                logger.warn("Can't remove environment variable `" + name + "` using reg.exe.", e);
                return false;
            }
        } catch (InvokeException e) {
            logger.warn("Can't remove environment variable `" + name + "` using setx.exe.", e);
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println(appendPath("C:\\Temp"));
    }
}
