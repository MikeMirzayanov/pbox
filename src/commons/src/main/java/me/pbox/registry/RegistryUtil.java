package me.pbox.registry;

import me.pbox.env.Environment;
import me.pbox.pkg.Pkg;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class RegistryUtil {
    public static void write(Pkg pkg, File pkgDir) {
        File registryFile = new File(Environment.getPboxRegistry(), pkg.getName() + "\\pbox.xml");

        //noinspection ResultOfMethodCallIgnored
        registryFile.getParentFile().mkdirs();

        try {
            FileUtils.copyFile(new File(pkgDir, "pbox.xml"), registryFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't copy to pbox.xml from " + pkgDir + " to the registry.", e);
        }
    }
}
