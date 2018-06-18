package me.pbox.command;

import me.pbox.env.Environment;
import me.pbox.env.Sources;
import me.pbox.http.HttpUtil;
import me.pbox.option.Opts;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class UpdateSelfCommand implements Command {
    private static final Logger logger = Logger.getLogger(UpdateSelfCommand.class);

    @Override
    public void run(Opts opts, String... args) {
        File binLstMd5File = new File(Environment.getPboxTemp(), "lst.md5");

        for (String source : Sources.getList()) {
            String binLstMd5Url = source + "/files/bin/lst.md5";
            HttpUtil.get(binLstMd5Url, binLstMd5File, true, true);
            if (binLstMd5File.isFile()) {
                List<String> lines;
                try {
                    lines = FileUtils.readLines(binLstMd5File);
                } catch (IOException e) {
                    throw new RuntimeException("Can't read '" + binLstMd5File + "'.", e);
                }

                for (String line : lines) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length == 2 && tokens[0].length() == 32
                            && tokens[1].length() > 1 && tokens[1].charAt(0) == '*') {
                        String md5 = tokens[0];
                        String fileName = tokens[1].substring(1);

                        File currentFile = new File(Environment.getPboxBin(), fileName);
                        try {
                            if (!currentFile.isFile() || !DigestUtils.md5Hex(FileUtils.readFileToByteArray(currentFile)).equalsIgnoreCase(md5)) {
                                logger.info("File 'bin/" + fileName + "' is missing or outdated.");
                                HttpUtil.get(source + "/files/bin/" + fileName, currentFile, true, true);
                                logger.info("File 'bin/" + fileName + "' has been updated.");
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        logger.info("Finished to update PBOX.");
    }
}
