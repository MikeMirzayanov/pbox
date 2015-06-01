package me.pbox.command;

import me.pbox.compress.CompressUtil;
import me.pbox.env.Environment;
import me.pbox.option.Opts;
import me.pbox.pkg.Pkg;
import me.pbox.pkg.PkgUtil;
import me.pbox.registry.RegistryUtil;
import me.pbox.xml.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class UninstallCommand extends DefaultCommand {
    private static final Logger logger = Logger.getLogger(UninstallCommand.class);

    protected void run(Pkg pkg) {
        logger.info("Processing the package '" + pkg + "'.");

        String packageName = pkg.getName();
        pkg = RegistryUtil.getInstalled(packageName);
        if (pkg == null) {
            logger.info("Can't find installed '" + packageName + "'.");
            return;
        }

        if (pkg.getVersion() == null) {
            throw new RuntimeException("Can't find any version of '" + pkg + "'. Are you sure the package exists?");
        }

        logger.info("Uninstalling '" + pkg + "'.");

        Opts opts;
        try {
            opts = RegistryUtil.getInstalledPackageOpts(pkg, getOpts());
        } catch (IOException e) {
            throw new RuntimeException("Can't uninstall " + pkg + ": " + e.getMessage(), e);
        }

        File pboxFile = PkgUtil.findPbox7zFile(pkg);
        if (pboxFile == null) {
            throw new RuntimeException("Can't find " + pkg + ".pbox.7z for '" + pkg + "'. Are you sure the package exists?");
        }

        final File pboxDir = new File(Environment.getPboxTempAsFile(), pkg.toString());
        CompressUtil.uncompress(pboxFile, pboxDir);

        DescriptorHandler descriptorHandler = new DescriptorHandler(pboxDir, "uninstall", opts);
        XmlUtil.traverse(Environment.getPboxXml(pboxDir), descriptorHandler);

        if (descriptorHandler.getProcessedStatements().isEmpty()) {
            throw new RuntimeException("Uninstallation is not supported for '" + pkg + "'.");
        }

        RegistryUtil.remove(pkg);

        File signalFile = new File(descriptorHandler.getOpts().get("homedir"), '.' + packageName + ".pbox");
        if (signalFile.isFile()) {
            //noinspection ResultOfMethodCallIgnored
            signalFile.delete();
        }

        File homeDir = new File(descriptorHandler.getOpts().get("homedir"));
        if (homeDir.isDirectory()) {
            File[] files = homeDir.listFiles();
            if (files != null && files.length == 0) {
                try {
                    FileUtils.forceDelete(homeDir);
                } catch (IOException e) {
                    throw new RuntimeException("Can't delete directory '" + homeDir + "'.");
                }
            }
        }

        logger.info("Package '" + pkg + "' uninstalled from the '" + descriptorHandler.getOpts().get("homedir") + "'.");
    }
}