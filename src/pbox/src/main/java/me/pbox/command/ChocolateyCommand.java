package me.pbox.command;

import me.pbox.chocolatey.ChocolateyUtil;
import me.pbox.option.Opts;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ChocolateyCommand implements Command {
    private static final Logger logger = Logger.getLogger(ChocolateyCommand.class);

    @Override
    public void run(Opts opts, String... args) {
        for (String arg : args) {
            runSingle(arg);
        }
    }

    private void runSingle(String packageName) {
        logger.info("Requesting https://chocolatey.org/ to get information about '" + packageName + "'.");
        ChocolateyUtil.Package pack = null;
        try {
            pack = ChocolateyUtil.grub(packageName);
        } catch (IOException e) {
            logger.warn("Can't get package '" + packageName + "' because of exception: " + e.getMessage(), e);
        }
        if (pack == null) {
            logger.info("*** No such package '" + packageName + "'");
        } else {
            logger.info("=======================================================");
            logger.info("Found '" + pack.getName() + "'.");
            logger.info("Description: " + pack.getDescription());
            logger.info("Icon URL: " + pack.getIconUrl());
            logger.info("Authors: " + pack.getAuthors());
            logger.info("Tags: " + StringUtils.join(pack.getTags(), ", "));
            logger.info("Versions: " + StringUtils.join(pack.getVersions(), ", "));

            for (String url : pack.getUrls()) {
                logger.info("* url: " + url);
            }
        }
    }
}
