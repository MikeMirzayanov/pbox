package me.pbox.registry;

import me.pbox.env.Environment;
import me.pbox.option.Opts;
import me.pbox.pkg.Pkg;
import me.pbox.xml.XmlUtil;
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
public class RegistryUtil {
    private static final Logger logger = Logger.getLogger(RegistryUtil.class);

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

    public static void remove(Pkg pkg) {
        File registryFile = new File(Environment.getPboxRegistry(), pkg.getName() + "\\pbox.xml");
        //noinspection ResultOfMethodCallIgnored
        registryFile.delete();
        try {
            FileUtils.deleteDirectory(registryFile.getParentFile());
        } catch (IOException e) {
            throw new RuntimeException("Can't delete '" + registryFile.getParentFile() + "'.");
        }
    }

    public static boolean isInstalled(Pkg pkg) {
        if (pkg.getVersion() == null) {
            throw new IllegalArgumentException("Unknown version of package '" + pkg + "'.");
        }

        File registryFile = new File(Environment.getPboxRegistry(), pkg.getName() + "\\pbox.xml");
        if (registryFile.isFile() && registryFile.length() > 0) {
            try {
                String version = XmlUtil.extractFromXml(registryFile, "/pbox/version", String.class);
                return pkg.getVersion().equals(version);
            } catch (IOException ignored) {
                // No operations.
            }
        }

        return false;
    }

    public static Opts getInstalledPackageOpts(Pkg pkg, Opts opts) throws IOException {
        if (pkg.getVersion() == null) {
            throw new IllegalArgumentException("Unknown version of package '" + pkg + "'.");
        }

        File pboxFile = new File(Environment.getPboxRegistry(), pkg.getName() + "\\." + pkg.getName() + ".pbox");

        List<String> lines = FileUtils.readLines(pboxFile);
        if (lines.size() < 2) {
            throw new IOException("File '" + pboxFile + "' should contain at least two lines.");
        }

        if (!lines.get(0).equals(pkg.toString())) {
            throw new IOException("Expected " + pkg + " in the first line of " + pboxFile + ".");
        }

        if (!lines.get(1).equals("opts:")) {
            throw new IOException("Expected " + pkg + " in the first line of " + pboxFile + ".");
        }

        Opts result = new Opts(opts);

        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i);
            int separatorIndex = line.indexOf('=');
            if (separatorIndex < 0) {
                throw new IOException("Can't find ''=' in line '" + line + "' of " + pboxFile + ".");
            }

            String key = line.substring(0, separatorIndex);
            String value = line.substring(separatorIndex + 1);

            result.put(key, value);
        }

        return result;
    }

    public static Pkg getInstalled(String packageName) {
        File registryFile = new File(Environment.getPboxRegistry(), packageName + "\\pbox.xml");

        if (registryFile.isFile() && registryFile.length() > 0) {
            try {
                String version = XmlUtil.extractFromXml(registryFile, "/pbox/version", String.class);
                Pkg pkg = new Pkg(version, packageName);
                String title = XmlUtil.extractFromXml(registryFile, "/pbox/title", String.class);
                if (StringUtils.isNoneBlank(title)) {
                    pkg.setTitle(title);
                }
                return pkg;
            } catch (IOException ignored) {
                // No operations.
            }
        }

        return null;
    }

    public static List<Pkg> findInstalled() {
        File[] packageDirs = new File(Environment.getPboxRegistry()).listFiles();
        List<Pkg> result = new ArrayList<>();

        if (packageDirs != null) {
            for (File packageDir : packageDirs) {
                if (packageDir.isDirectory()) {
                    Pkg pkg = getInstalled(packageDir.getName());
                    if (pkg != null) {
                        result.add(pkg);
                    }
                }
            }
        }

        return result;
    }

    public static void reset() {
        int result = 0;

        File[] packageDirs = new File(Environment.getPboxRegistry()).listFiles();
        if (packageDirs != null) {
            for (File packageDir : packageDirs) {
                if (packageDir.isDirectory()) {
                    Pkg pkg = getInstalled(packageDir.getName());
                    if (pkg != null) {
                        logger.info("Forgetting " + pkg + ".");
                        result++;
                    }
                    try {
                        FileUtils.forceDelete(packageDir);
                    } catch (IOException e) {
                        throw new RuntimeException("Unable to completely forget package " + pkg + ".", e);
                    }
                }
            }
        }

        logger.info("Forgot " + result + " package(s) in total.");
    }

    public static void forget(Pkg pkg) {
        File packageDir = new File(Environment.getPboxRegistry(), pkg.getName());
        if (packageDir.isDirectory()) {
            logger.info("Forgetting " + pkg + ".");
            try {
                FileUtils.forceDelete(packageDir);
            } catch (IOException e) {
                throw new RuntimeException("Unable to completely forget package " + pkg + ".", e);
            }
        }
    }
}
