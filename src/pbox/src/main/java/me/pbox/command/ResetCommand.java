package me.pbox.command;

import me.pbox.Console;
import me.pbox.option.Opts;
import me.pbox.registry.RegistryUtil;
import org.apache.log4j.Logger;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ResetCommand implements Command {
    private static final Logger logger = Logger.getLogger(ResetCommand.class);

    @Override
    public void run(Opts opts, String... args) {
        boolean force = opts.has("force");

        if (!force) {
            logger.info("All information about installed packages will be deleted, but packages will leave." +
                    " The operation is exactly the same as 'forget all'." +
                    " Are you sure you want to RESET the local registry [Y/no]?");
            String line = Console.readln();
            force = line != null && line.equals("Y");
        }

        if (force) {
            logger.info("Resetting the local repository...");
            RegistryUtil.reset();
            logger.info("Done.");
        } else {
            logger.info("Operation aborted.");
        }
    }
}
