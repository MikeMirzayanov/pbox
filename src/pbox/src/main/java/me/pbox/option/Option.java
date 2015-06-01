package me.pbox.option;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public enum Option {
    OFFLINE(
            Arrays.asList("install", "uninstall", "package", "choco"),
            Arrays.asList("o", "offline"),
            "Switches off PBOX initial check for updates, makes startup faster."
    ),

    VERSION(
            Arrays.asList("install", "package"),
            Arrays.asList("v", "version"),
            "Version of a package to be processed."
    ),

    HELP(
            Arrays.asList("", "install", "uninstall", "package", "choco"),
            Arrays.asList("h", "help", "?"),
            "Prints common or command help."
    ),

    PACKAGE_DIR(
            Arrays.asList("package"),
            Arrays.asList("pd", "packageDir"),
            "Directory to put built packages, new package will be in <targetDir>\\<packageName>\\<version>."
    ),

    TEMPLATE_DIR(
            Arrays.asList("package"),
            Arrays.asList("td", "templateDir"),
            "Directory to find package templates, you may use public templates like <pbox-git-dir>\\templates"
    );

    private List<String> commands;
    private List<String> names;
    private String help;

    Option(List<String> commands, List<String> names, String help) {
        this.commands = commands;
        this.names = names;
        this.help = help;
    }

    public List<String> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    public String getHelp() {
        return help;
    }

    public String getRequiredMessage() {
        String longest = getCanonicalName();
        return "Option '" + longest + "' is required. Please use --" + longest + "=<value> to set it.";
    }

    public String getCanonicalName() {
        String longest = "";
        for (String name : names) {
            if (name.length() > longest.length()) {
                longest = name;
            }
        }
        return longest;
    }

    public void throwRequiredException() {
        throw new RuntimeException(getRequiredMessage());
    }
}
