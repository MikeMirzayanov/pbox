package me.pbox;

import me.pbox.command.*;
import me.pbox.env.Environment;
import me.pbox.option.Option;
import me.pbox.option.Opts;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    private static String[] parseCommandLineOpts(String[] args, Opts opts) {
        List<String> cmds = new ArrayList<>();

        for (String arg : args) {
            if (arg == null) {
                continue;
            }

            if (arg.startsWith("-")) {
                addOpt(arg, opts);
            } else {
                cmds.add(arg);
            }
        }

        return cmds.toArray(new String[cmds.size()]);
    }

    private static void addOpt(String arg, Opts opts) {
        String keyValue = null;

        if (arg.startsWith("--")) {
            keyValue = arg.substring(2);
        } else {
            if (arg.startsWith("-")) {
                keyValue = arg.substring(1);
            }
        }

        if (keyValue != null) {
            int sepIndex = keyValue.indexOf('=');
            if (sepIndex < 0) {
                opts.put(preprocessOpt(keyValue), "");
            } else {
                opts.put(
                        preprocessOpt(keyValue.substring(0, sepIndex)),
                        preprocessOpt(keyValue.substring(sepIndex + 1))
                );
            }
        }
    }

    private static String preprocessOpt(String s) {
        if (s == null) {
            return "";
        }

        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }

        if (s.startsWith("\'") && s.endsWith("\'")) {
            return s.substring(1, s.length() - 1);
        }

        return s;
    }


    private static void help() {
        Console.println("Usage:\n\tpbox <command> <arguments>\nwhere\n\t<command>:   install or uninstall\n\t<arguments>: one or more packages");

        //noinspection CallToSystemExit
        System.exit(1);
    }

    public static void main(String[] args) {
        Opts opts = new Opts();
        String[] commandLineArgs = parseCommandLineOpts(args, opts);

        if (commandLineArgs.length == 0 || opts.has(Option.HELP)) {
            help();
        }

        String command = commandLineArgs[0];
        String[] arguments = new String[commandLineArgs.length - 1];
        System.arraycopy(commandLineArgs, 1, arguments, 0, arguments.length);

        try {
            switch (command) {
                case "update-self":
                    new UpdateSelfCommand().run(opts, arguments);
                    break;

                case "install":
                    new InstallCommand().run(opts, arguments);
                    break;

                case "uninstall":
                    new UninstallCommand().run(opts, arguments);
                    break;

                case "package":
                    new PackageCommand().run(opts, arguments);
                    break;

                case "find":
                case "search":
                    new FindCommand().run(opts, arguments);
                    break;

                case "list":
                    new ListCommand().run(opts, arguments);
                    break;

                case "list-installed":
                    new ListInstalledCommand().run(opts, arguments);
                    break;

                case "reset":
                    new ResetCommand().run(opts, arguments);
                    break;

                case "info":
                    new InfoCommand().run(opts, arguments);
                    break;

                case "forget":
                    new ForgetCommand().run(opts, arguments);
                    break;

                case "choco":
                case "chocolatey":
                    new ChocolateyCommand().run(opts, arguments);
                    break;

                default:
                    Console.println("Unknown command '" + command + '\'');
            }
        } catch (RuntimeException e) {
            logger.fatal("Unexpected error: " + e.getMessage(), e);

            //noinspection CallToSystemExit
            System.exit(1);
        } finally {
            FileUtils.deleteQuietly(Environment.getPboxTempAsFile());
        }
    }
}
