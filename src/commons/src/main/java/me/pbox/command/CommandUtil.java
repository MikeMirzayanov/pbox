package me.pbox.command;

import me.pbox.env.EnvironmentUtil;
import me.pbox.invoke.InvokeException;
import me.pbox.invoke.InvokeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class CommandUtil {
    private static List<String> splitCommandLine(String commandLine) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(commandLine)) {
            return result;
        }

        StringBuilder sb = new StringBuilder();
        int quoteCount = 0;

        for (int i = 0; i < commandLine.length(); i++) {
            char c = commandLine.charAt(i);

            if (Character.isWhitespace(c) && quoteCount % 2 == 0) {
                if (sb.length() > 0) {
                    result.add(sb.toString().trim());
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

    public static void path(File pboxPkgDir, String item, File homeDir) {
        EnvironmentUtil.appendPath(item);
    }

    public static void env(File pboxPkgDir, String item, File homeDir) {
        String[] keyAndValue = item.split("=");
        if (keyAndValue.length != 2 || StringUtils.isBlank(keyAndValue[0]) || keyAndValue[1] == null) {
            throw new IllegalArgumentException("Expected exactly form key=value while setting env.");
        }
        EnvironmentUtil.setEnvironmentVariable(keyAndValue[0], keyAndValue[1]);
    }

    public static void msi(File pboxPkgDir, String item, File homeDir) {
        String[] nameAndParams = item.split("=");
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

        List<String> commandLineParts = new ArrayList<>();
        commandLineParts.add("msiexec.exe");
        commandLineParts.add("/i");
        commandLineParts.add(msiFile.getAbsolutePath());
        commandLineParts.add(msiFile.getAbsolutePath());
        commandLineParts.addAll(splitCommandLine(nameAndParams[1]));

        try {
            InvokeUtil.run(pboxPkgDir, commandLineParts.toArray(new String[commandLineParts.size()]));
        } catch (InvokeException e) {
            throw new RuntimeException("Can't run msi '" + StringUtils.join(commandLineParts, ' ') + "'.");
        }
    }

    public static void script(File pboxPkgDir, String item, File homeDir) {
        File scriptFile = new File(item);

        //noinspection ResultOfMethodCallIgnored
        homeDir.mkdirs();

        if (item.startsWith("./") || item.startsWith(".\\")) {
            scriptFile = new File(pboxPkgDir, item.substring(2));
        }

        if (!scriptFile.isFile()) {
            throw new RuntimeException("Can't find script '" + scriptFile + "'.");
        }

        try {
            InvokeUtil.run(pboxPkgDir, "cmd.exe", "/C", scriptFile.getAbsolutePath(), homeDir.getAbsolutePath());
        } catch (InvokeException e) {
            throw new RuntimeException("Can't run script '" + scriptFile + "'.");
        }
    }

    public static void main(String[] args) {
        script(new File("C:\\Programs\\p box\\temp\\59674\\runexe$4.4"), ".\\scripts\\a.bat", new File("C:\\rr"));
    }
}
