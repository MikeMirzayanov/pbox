package me.pbox.command;

import me.pbox.env.Environment;
import me.pbox.env.EnvironmentUtil;
import me.pbox.invoke.InvokeException;
import me.pbox.invoke.InvokeUtil;
import me.pbox.option.Opts;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class CommandUtil {
    private static final Logger logger = Logger.getLogger(CommandUtil.class);

    private static List<String> splitCommandLine(String commandLine) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(commandLine)) {
            return result;
        }

        StringBuilder sb = new StringBuilder();
        int quoteCount = 0;

        for (int i = 0; i < commandLine.length(); i++) {
            char c = commandLine.charAt(i);

            if (c == '"') {
                quoteCount++;
            }

            if (Character.isWhitespace(c) && quoteCount % 2 == 0) {
                if (sb.length() > 0) {
                    String item = sb.toString().trim();
                    result.add(item);
                    sb = new StringBuilder();
                }
            } else {
                sb.append(c);
            }
        }

        if (sb.length() > 0) {
            result.add(sb.toString().trim());
        }

        return result;
    }

    public static void copy(File pboxPkgDir, String item, File homeDir) {
        File copyFile = new File(item);

        if (item.startsWith("\"") && item.endsWith("\"")) {
            item = item.substring(1, item.length() - 1);
        }

        //noinspection ResultOfMethodCallIgnored
        homeDir.mkdirs();

        if (item.startsWith("./") || item.startsWith(".\\")) {
            copyFile = new File(pboxPkgDir, item.substring(2));
        }

        if (!copyFile.exists()) {
            throw new RuntimeException("Can't find '" + copyFile + "'.");
        }

        if (copyFile.isFile()) {
            try {
                InvokeUtil.run(pboxPkgDir, "xcopy.exe", "/V", "/H", "/R", "/Y", copyFile.getAbsolutePath(), homeDir.toString());
            } catch (InvokeException e) {
                throw new RuntimeException("Can't copy '" + copyFile + "' to '" + homeDir + "'.", e);
            }
        } else {
            try {
                InvokeUtil.run(pboxPkgDir, "xcopy.exe", "/V", "/H", "/R", "/Y", "/E", copyFile.getAbsolutePath(), homeDir.toString());
            } catch (InvokeException e) {
                throw new RuntimeException("Can't copy '" + copyFile + "' to '" + homeDir + "'.", e);
            }
        }
    }

    public static void remove(File pboxPkgDir, String item, File homeDir) {
        File removeFile = new File(item);

        if (item.startsWith("./") || item.startsWith(".\\")) {
            removeFile = new File(pboxPkgDir, item.substring(2));
        }

        if (!removeFile.exists()) {
            return;
        }

        try {
            FileUtils.forceDelete(removeFile);
        } catch (IOException ignore) {
            // No operations.
        }

        if (removeFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            removeFile.delete();
        }

        if (removeFile.exists()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
                // No operations.
            }

            try {
                FileUtils.forceDelete(removeFile);
            } catch (IOException ignore) {
                // No operations.
            }
        }
    }

    public static void path(File pboxPkgDir, String item, File homeDir, Opts opts) {
        if (opts.has("pp") || opts.has("prepend-path")) {
            EnvironmentUtil.prependPath(item);
        } else {
            EnvironmentUtil.appendPath(item);
        }
    }

    public static void unpath(File pboxDir, String item, File homedir) {
        EnvironmentUtil.removePath(item);
    }

    public static void exec(File pboxPkgDir, String item, File homeDir) {
        try {
            List<String> cmd = new ArrayList<>();

            for (String line : splitCommandLine(item)) {
                if (line.startsWith(".\\") || line.startsWith("./")) {
                    cmd.add(pboxPkgDir + "\\" + line.substring(2));
                } else {
                    if (line.startsWith("\".\\") || line.startsWith("\"./")) {
                        cmd.add("\"" + pboxPkgDir + "\\" + line.substring(3));
                    } else {
                        cmd.add(line);
                    }
                }
            }

            String joinedCommandLine = StringUtils.join(cmd, " ");

            logger.info("Starting '" + joinedCommandLine + "' in '" + pboxPkgDir + "'.");
            long startTimeMillis = System.currentTimeMillis();
            Process process = new ProcessBuilder().command(cmd).directory(pboxPkgDir).inheritIO().start();
            int exitCode = process.waitFor();
            logger.info("Finished '" + joinedCommandLine + "' in " + (System.currentTimeMillis() - startTimeMillis)
                    + " ms and exit code " + exitCode + ".");
        } catch (Exception e) {
            throw new RuntimeException("Can't run '" + item + "'.");
        }
    }

    public static void env(File pboxPkgDir, String item, File homeDir) {
        String[] keyAndValue = item.split("=");
        if (keyAndValue.length != 2 || StringUtils.isBlank(keyAndValue[0]) || keyAndValue[1] == null) {
            throw new IllegalArgumentException("Expected exactly form key=value while setting env.");
        }
        EnvironmentUtil.setEnvironmentVariable(keyAndValue[0], keyAndValue[1]);
    }

    public static void unenv(File pboxDir, String item, File homedir) {
        EnvironmentUtil.removeEnvironmentVariable(item);
    }

    public static void msi(File pboxPkgDir, String item, File homeDir) {
        int equalIndex = item.indexOf('=');
        if (equalIndex < 0) {
            throw new IllegalArgumentException("Expected exactly form .\\path\\to\\msi=params.");
        }

        String[] nameAndParams = {item.substring(0, equalIndex), item.substring(equalIndex + 1)};
        if (nameAndParams.length != 2 || StringUtils.isBlank(nameAndParams[0]) || nameAndParams[1] == null) {
            throw new IllegalArgumentException("Expected exactly form key=value while running msi.");
        }

        File msiFile = new File(nameAndParams[0]);
        if (nameAndParams[0].startsWith("./") || nameAndParams[0].startsWith(".\\")) {
            msiFile = new File(pboxPkgDir, nameAndParams[0].substring(2));
        }

        if (!msiFile.isFile()) {
            throw new RuntimeException("Can't find msi file '" + msiFile + "'.");
        }

        try {
            FileUtils.copyFileToDirectory(msiFile, Environment.getPboxMsiDirectory());
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to copy file '" + msiFile
                    + "' to the directory '" + Environment.getPboxMsiDirectory() + "'.", e);
        }

        List<String> commandLineParts = new ArrayList<>();
        commandLineParts.add("msiexec.exe");
        commandLineParts.add("/i");
        commandLineParts.add("\"" + new File(Environment.getPboxMsiDirectory(), msiFile.getName()) + "\"");
        commandLineParts.addAll(splitCommandLine(nameAndParams[1]));

        StringBuilder commandLine = new StringBuilder();
        for (String commandLinePart : commandLineParts) {
            if (commandLine.length() > 0) {
                commandLine.append(' ');
            }
            commandLine.append(commandLinePart.trim());
        }

        try {
            File msiexecRunFile = new File(pboxPkgDir, "msiexecRunCmd.bat");
            FileUtils.write(msiexecRunFile, commandLine);
            InvokeUtil.run(pboxPkgDir, "cmd.exe", "/C", msiexecRunFile.getAbsolutePath());
        } catch (InvokeException | IOException e) {
            throw new RuntimeException("Can't run msi '" + StringUtils.join(commandLineParts, ' ') + "'.", e);
        } finally {
            FileUtils.deleteQuietly(new File(Environment.getPboxMsiDirectory(), msiFile.getName()));
        }
    }

    public static void unmsi(File pboxPkgDir, String item, File homeDir) {
        int equalIndex = item.indexOf('=');
        if (equalIndex < 0) {
            throw new IllegalArgumentException("Expected exactly form .\\path\\to\\msi=params.");
        }

        String[] nameAndParams = {item.substring(0, equalIndex), item.substring(equalIndex + 1)};
        if (nameAndParams.length != 2 || StringUtils.isBlank(nameAndParams[0]) || nameAndParams[1] == null) {
            throw new IllegalArgumentException("Expected exactly form key=value while running msi.");
        }

        File msiFile = new File(nameAndParams[0]);
        if (nameAndParams[0].startsWith("./") || nameAndParams[0].startsWith(".\\")) {
            msiFile = new File(pboxPkgDir, nameAndParams[0].substring(2));
        }

        if (!msiFile.isFile()) {
            throw new RuntimeException("Can't find msi file '" + msiFile + "'.");
        }

        try {
            FileUtils.copyFileToDirectory(msiFile, Environment.getPboxMsiDirectory());
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to copy file '" + msiFile
                    + "' to the directory '" + Environment.getPboxMsiDirectory() + "'.", e);
        }

        List<String> commandLineParts = new ArrayList<>();
        commandLineParts.add("msiexec.exe");
        commandLineParts.add("/x");
        commandLineParts.add(new File(Environment.getPboxMsiDirectory(), msiFile.getName()).getAbsolutePath());
        commandLineParts.addAll(splitCommandLine(nameAndParams[1]));

        try {
            InvokeUtil.run(pboxPkgDir, commandLineParts.toArray(new String[commandLineParts.size()]));
        } catch (InvokeException e) {
            throw new RuntimeException("Can't run msi '" + StringUtils.join(commandLineParts, ' ') + "'.", e);
        } finally {
            FileUtils.deleteQuietly(new File(Environment.getPboxMsiDirectory(), msiFile.getName()));
        }
    }

    public static void script(File pboxPkgDir, String item, File homeDir) {
        List<String> commandLine = splitCommandLine(item);

        File scriptFile = new File(commandLine.get(0));

        if (commandLine.get(0).startsWith("./") || commandLine.get(0).startsWith(".\\")) {
            scriptFile = new File(pboxPkgDir, commandLine.get(0).substring(2));
        }

        if (commandLine.get(0).startsWith("\"./") || commandLine.get(0).startsWith("\".\\")) {
            scriptFile = new File(pboxPkgDir, commandLine.get(0).substring(3, commandLine.get(0).length() - 1));
        }

        if (!scriptFile.isFile()) {
            throw new RuntimeException("Can't find script '" + scriptFile + "'.");
        }

        //noinspection ResultOfMethodCallIgnored
        homeDir.mkdirs();

        commandLine.set(0, scriptFile.getAbsolutePath());
        String[] args = new String[commandLine.size() + 2];
        args[0] = "cmd.exe";
        args[1] = "/C";
        args[2] = "\"" + scriptFile.getAbsolutePath() + "\"";
        for (int i = 1; i < commandLine.size(); i++) {
            args[i + 2] = commandLine.get(i);
        }

        args[2] = "\"" + args[2];
        args[args.length - 1] = args[args.length - 1] + "\"";

        try {
            InvokeUtil.run(pboxPkgDir, args);
        } catch (InvokeException e) {
            throw new RuntimeException("Can't run script '" + scriptFile + "'.");
        }
    }

    public static void main(String[] args) {
//        List<String> strings = splitCommandLine("\"exe\\Sublime Text Build 3065 Setup.exe\" /VERYSILENT /NORESTART /TASKS=\"contextentry\" /DIR=\"${hom edir}\"");
//        System.out.println(strings.size());
//        for (String string : strings) {
//            System.out.println("'" + string + "'");
//        }

        exec(
                new File("C:\\ProgramData\\pbox\\temp\\99962\\sublime-text-3$3.3.0.0.3065"),
                //"\"exe\\Sublime Text Build 3065 Setup.exe\" /?",
                "\".\\exe\\Sublime Text Build 3065 Setup.exe\" /?",
                new File(".")
        );
    }
}
