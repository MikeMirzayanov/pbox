package me.pbox;

import me.pbox.command.InstallCommand;
import org.apache.log4j.Logger;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    private static void help() {
        System.out.println("Usage:\n\tpbox <command> <arguments>\nwhere\n\t<command>:   install or uninstall\n\t<arguments>: one or more packages");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            help();
        }

        String command = args[0];

        String[] arguments = new String[args.length - 1];
        System.arraycopy(args, 1, arguments, 0, args.length - 1);

        try {
        switch (command) {
            case "install":
                new InstallCommand().run(arguments);
                break;

            default:
                System.out.println("Unknown command `" + command + "`");
        }
        } catch (RuntimeException e) {
            logger.fatal("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
}
