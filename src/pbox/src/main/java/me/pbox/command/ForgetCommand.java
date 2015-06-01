package me.pbox.command;

import me.pbox.Console;
import me.pbox.pkg.Pkg;
import me.pbox.registry.RegistryUtil;
import org.apache.log4j.Logger;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ForgetCommand extends DefaultCommand {
    private static final Logger logger = Logger.getLogger(ForgetCommand.class);

    @Override
    protected void run(Pkg pkg) {
        boolean force = getOpts().has("force");

        if (!force) {
            logger.info("All information about the installed package " + pkg + " will be deleted, but it will not be uninstalled." +
                    " Are you sure you want to FORGET the " + pkg + " [Y/no]?");
            String line = Console.readln();
            force = line != null && line.equals("Y");
        }

        if (force) {
            RegistryUtil.forget(pkg);
            logger.info("Done.");
        } else {
            logger.info("Operation aborted.");
        }
    }
}
