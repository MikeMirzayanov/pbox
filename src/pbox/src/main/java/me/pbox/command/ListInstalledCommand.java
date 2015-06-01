package me.pbox.command;

import me.pbox.option.Opts;
import me.pbox.pkg.Pkg;
import me.pbox.registry.RegistryUtil;

import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ListInstalledCommand implements Command {
    @Override
    public void run(Opts opts, String... args) {
        List<Pkg> packages = RegistryUtil.findInstalled();

        FindCommand.printPackageCount(packages.size());
        int index = 0;
        for (Pkg p : packages) {
            index++;
            FindCommand.printPackage(index, p.toMap());
        }
    }
}
