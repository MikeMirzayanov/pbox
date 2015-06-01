package me.pbox.command;

import me.pbox.option.Opts;

import java.util.List;
import java.util.Map;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ListCommand implements Command {
    @Override
    public void run(Opts opts, String... args) {
        List<Map<String, Object>> packages = FindCommand.findPackages(new String[]{}, opts.has("all"));

        FindCommand.printPackageCount(packages.size());
        int index = 0;
        for (Map<String, Object> p : packages) {
            index++;
            FindCommand.printPackage(index, p);
        }
    }
}
