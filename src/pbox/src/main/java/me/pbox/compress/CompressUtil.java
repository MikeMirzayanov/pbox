package me.pbox.compress;

import me.pbox.env.Environment;
import me.pbox.invoke.InvokeException;
import me.pbox.invoke.InvokeUtil;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class CompressUtil {
    private static final Logger logger = Logger.getLogger(CompressUtil.class);

    public static boolean uncompress(File archiveFile, File uncompressDirectory) {
        logger.info("Uncompressing '" + archiveFile + "' to the '" + uncompressDirectory + "'.");

        if (archiveFile == null || !archiveFile.isFile() || archiveFile.length() == 0) {
            throw new IllegalArgumentException("Illegal file to extract '" + archiveFile + "'.");
        }

        //noinspection ResultOfMethodCallIgnored
        uncompressDirectory.mkdirs();

        if (!uncompressDirectory.isDirectory()) {
            throw new IllegalArgumentException("Illegal directory to extract '" + uncompressDirectory + "'.");
        }

        try {
            long size = archiveFile.length();

            long beforeTimeMillis = System.currentTimeMillis();
            boolean result = InvokeUtil.run(true, true, Environment.getBin("7za"), "x", "-y", "-o" + uncompressDirectory, archiveFile.getAbsolutePath()) == 0;
            long durationTimeMillis = System.currentTimeMillis() - beforeTimeMillis;

            if (result) {
                logger.info("Successfully uncompressed '" + archiveFile + "' to the '" + uncompressDirectory + "' [" + size + " bytes in " + durationTimeMillis + " ms].");
            } else {
                logger.info("Failed to uncompress '" + archiveFile + "' to the '" + uncompressDirectory + "' [in " + durationTimeMillis + " ms].");
            }

            return result;
        } catch (InvokeException e) {
            logger.warn("Can't uncompress file '" + archiveFile + "' to the directory '" + uncompressDirectory + "'.", e);
            return false;
        }
    }

    public static boolean compress(File targetFile, File compressDirectory) {
        logger.info("Compressing '" + compressDirectory + "' to the '" + targetFile + "'.");

        if (compressDirectory == null || !compressDirectory.isDirectory()) {
            throw new IllegalArgumentException("No such directory to compress '" + compressDirectory + "'.");
        }

        //noinspection ResultOfMethodCallIgnored
        targetFile.getParentFile().mkdirs();

        try {
            long beforeTimeMillis = System.currentTimeMillis();
            boolean result = InvokeUtil.run(true, true, Environment.getBin("7za"), "a", "-mx9", targetFile.getAbsolutePath(), compressDirectory.getAbsolutePath() + "\\*") == 0;
            long durationTimeMillis = System.currentTimeMillis() - beforeTimeMillis;

            if (result) {
                logger.info("Successfully compressed '" + compressDirectory + "' to the '" + targetFile + "' [" + targetFile.length() + " bytes in " + durationTimeMillis + " ms].");
            } else {
                logger.info("Failed to compress '" + compressDirectory + "' to the '" + targetFile + "' [in " + durationTimeMillis + " ms].");
            }

            return result;
        } catch (InvokeException e) {
            logger.warn("Can't compress directory '" + compressDirectory + "' to the file '" + targetFile + "'.", e);
            return false;
        }
    }
}
