package me.pbox.command;

import me.pbox.compress.CompressUtil;
import me.pbox.env.Environment;
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
public class InstallCommand extends DefaultCommand {
    private static final Logger logger = Logger.getLogger(InstallCommand.class);

    @Override
    protected void run(Pkg pkg) {
        logger.info("Processing the package '" + pkg + "'.");

        if (getOpts().has("version")) {
            pkg.setVersion(getOpts().get("version"));
        } else {
            pkg.setLatestVersion();
        }

        if (pkg.getVersion() == null) {
            throw new RuntimeException("Can't find any version of '" + pkg + "'. Are you sure the package exists?");
        }

        logger.info("Installing '" + pkg + "'.");

        if (RegistryUtil.isInstalled(pkg)) {
            logger.info("Package '" + pkg + "' is already installed.");
            return;
        }

        File pboxFile = PkgUtil.findPbox7zFile(pkg);
        if (pboxFile == null) {
            throw new RuntimeException("Can't find " + pkg + ".pbox.7z for '" + pkg + "'. Are you sure the package exists?");
        }

        final File pboxDir = new File(Environment.getPboxTempAsFile(), pkg.toString());
        CompressUtil.uncompress(pboxFile, pboxDir);

        DescriptorHandler descriptorHandler =  new DescriptorHandler(pboxDir, "install", getOpts());
        XmlUtil.traverse(Environment.getPboxXml(pboxDir), descriptorHandler);

        RegistryUtil.write(pkg, pboxDir);
        File signalFile = new File(descriptorHandler.getOpts().get("homedir"), '.' + pkg.getName() + ".pbox");
        try {
            FileUtils.write(signalFile, pkg.toString() + '\n' + descriptorHandler.getOpts());
        } catch (IOException e) {
            throw new RuntimeException("Can't write signal file " + signalFile + '.', e);
        }

        signalFile = new File(Environment.getPboxRegistryAsFile(), pkg.getName() + "\\." + pkg.getName() + ".pbox");
        try {
            FileUtils.write(signalFile, pkg.toString() + '\n' + descriptorHandler.getOpts());
        } catch (IOException e) {
            throw new RuntimeException("Can't write signal file " + signalFile + '.', e);
        }

        logger.info("Package '" + pkg + "' installed to the '" + descriptorHandler.getOpts().get("homedir") + "'.");
    }
}
