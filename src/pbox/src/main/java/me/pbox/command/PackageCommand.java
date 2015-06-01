package me.pbox.command;

import me.pbox.chocolatey.ChocolateyUtil;
import me.pbox.compress.CompressUtil;
import me.pbox.http.HttpUtil;
import me.pbox.option.Option;
import me.pbox.pkg.Pkg;
import me.pbox.xml.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class PackageCommand extends DefaultCommand {
    private static final Logger logger = Logger.getLogger(PackageCommand.class);

    @Override
    protected void run(Pkg pkg) {
        String name = pkg.getName();

        if (!getOpts().has(Option.TEMPLATE_DIR)) {
            Option.TEMPLATE_DIR.throwRequiredException();
        } else {
            if (!new File(getOpts().get(Option.TEMPLATE_DIR)).isDirectory()) {
                throw new RuntimeException("'" + Option.TEMPLATE_DIR.getCanonicalName() + "' should be a directory.");
            }
        }

        if (!getOpts().has(Option.PACKAGE_DIR)) {
            Option.PACKAGE_DIR.throwRequiredException();
        }

        String packageDescriptor = readFile(getOpts().get(Option.TEMPLATE_DIR), name, "package.xml");
        final List<ChocolateyUtil.Package> chocolateyPackages = new ArrayList<>();

        XmlUtil.traverse(packageDescriptor, new DefaultHandler() {
            private String content;

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                content += new String(ch, start, length);
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                content = "";

                if (localName.equals("chocolatey")) {
                    String name = AttributeUtil.getValue(attributes, "name");
                    if (StringUtils.isBlank(name)) {
                        throw new RuntimeException("package.xml: Attribute chocolatey:name should be non-empty.");
                    }

                    try {
                        chocolateyPackages.add(ChocolateyUtil.grub(name));
                    } catch (IOException e) {
                        throw new RuntimeException("Can't grub chocolatey package '" + name + "'.", e);
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (localName.equals("title") && StringUtils.isNoneBlank(content)) {
                    getOpts().put("title", content);
                }
            }
        });

        String version = getOpts().get(Option.VERSION);

        try {
            if (!chocolateyPackages.isEmpty() && StringUtils.isBlank(version)) {
                ChocolateyUtil.Package chocolateyPackage = chocolateyPackages.get(0);
                if (chocolateyPackage.getVersions().isEmpty()) {
                    throw new RuntimeException("Unable to find versions in linked chocolatey package '" + chocolateyPackage.getName() + "'.");
                }
                version = chocolateyPackage.getVersions().get(0);
                processVersion(name, version, chocolateyPackage);
                return;
            }

            if (StringUtils.isNoneBlank(version)) {
                ChocolateyUtil.Package chocolateyPackage = chocolateyPackages.isEmpty() ? null : chocolateyPackages.get(0);
                processVersion(name, version, chocolateyPackage);
            } else {
                throw new RuntimeException("Can't find option 'version' in command line or get linked chocolatey package.");
            }
        } finally {
            try {
                File templateVersionDir = new File(new File(getOpts().get(Option.TEMPLATE_DIR), name), version);
                FileUtils.forceDelete(templateVersionDir);
            } catch (IOException ignored) {
                // No operations.
            }
        }
    }

    private void processVersion(final String packageName, String version,
                                final ChocolateyUtil.Package chocolateyPackage) {
        getOpts().put("version", version);

        File outputDir = new File(getOpts().get(Option.PACKAGE_DIR));
        File templateDir = new File(getOpts().get(Option.TEMPLATE_DIR));

        //noinspection ResultOfMethodCallIgnored
        outputDir.mkdirs();

        String packageDescriptor = readFile(templateDir.getAbsolutePath(), packageName, "package.xml");
        String pboxDescriptor = readFile(templateDir.getAbsolutePath(), packageName, "pbox.xml");

        final File templateVersionDir = new File(templateDir, packageName + "\\" + version);
        final File outputVersionDir = new File(outputDir, packageName + "\\" + version);

        if (templateVersionDir.exists()) {
            try {
                FileUtils.forceDelete(templateVersionDir);
            } catch (IOException e) {
                throw new RuntimeException("Can't delete '" + templateVersionDir + "'.");
            }
        }

        try {
            String descriptorPackageName = XmlUtil.extractFromXml(new ByteArrayInputStream(pboxDescriptor.getBytes()), "/pbox/name", String.class);
            if (!packageName.equals(descriptorPackageName)) {
                throw new RuntimeException("Expected package name '" + packageName + "' but found '" + descriptorPackageName + "'.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't get package name from 'pbox.xml'", e);
        }

        if (chocolateyPackage != null && StringUtils.isNoneBlank(chocolateyPackage.getDescription())) {
            getOpts().put(".description.pbox", chocolateyPackage.getDescription());
        }

        if (chocolateyPackage != null && StringUtils.isNoneBlank(chocolateyPackage.getAuthors())) {
            getOpts().put(".authors.pbox", chocolateyPackage.getAuthors());
        }

        if (chocolateyPackage != null && StringUtils.isNoneBlank(chocolateyPackage.getIconUrl())) {
            getOpts().put(".iconUrl.pbox", chocolateyPackage.getIconUrl());
        }

        final Set<String> tags = new TreeSet<>();
        if (chocolateyPackage != null) {
            for (String tag : chocolateyPackage.getTags()) {
                if (!tag.trim().isEmpty()) {
                    tags.add(prepareTag(tag));
                }
            }
        }

        // Check if it is built.
        {
            File targetPackageFile = new File(outputVersionDir, packageName + "$" + version + ".pbox.7z");
            File targetDescriptorFile = new File(outputVersionDir, "pbox.xml");
            if (targetPackageFile.isFile() && targetPackageFile.length() > 0
                    && targetDescriptorFile.isFile() && targetDescriptorFile.length() > 0) {
                logger.info("Package version is already exists: " + targetDescriptorFile + " and " + targetPackageFile + ".");
                return;
            }
        }

        //noinspection ResultOfMethodCallIgnored
        templateVersionDir.mkdirs();

        XmlUtil.traverse(packageDescriptor, new DefaultHandler() {
            private String content;

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                content += new String(ch, start, length);
            }

            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                content = "";

                if (localName.equals("download")) {
                    String url = AttributeUtil.getValue(attributes, "url");
                    String to = AttributeUtil.getValue(attributes, "to");

                    if (StringUtils.isBlank(url)) {
                        throw new RuntimeException("package.xml: Attribute download:url should be non-empty.");
                    }

                    if (StringUtils.isBlank(to)) {
                        throw new RuntimeException("package.xml: Attribute download:to should be non-empty.");
                    }

                    if (url.startsWith("chocolatey.urls[")) {
                        int index = Integer.valueOf(url.substring("chocolatey.urls[".length(), url.length() - 1));
                        if (chocolateyPackage == null) {
                            throw new RuntimeException("package.xml: Invalid URL because of chocolateyPackage==null but url='"
                                    + url + "'.");
                        }
                        if (index < 0 | index >= chocolateyPackage.getUrls().size()) {
                            throw new RuntimeException("package.xml: Invalid URL index in "
                                    + url + ", but there are " + chocolateyPackage.getUrls().size() + " urls.");
                        }
                        url = chocolateyPackage.getUrls().get(index);
                    }

                    try {
                        new URL(url);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("package.xml: Invalid url '" + url + "'.");
                    }

                    File targetFile = new File(templateVersionDir, to);
                    if (to.endsWith("\\") || to.endsWith("/")) {
                        String fileName;
                        try {
                            fileName = new File(new URL(url).getFile()).getName();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException("package.xml: Invalid url '" + url + "'.");
                        }
                        targetFile = new File(targetFile, fileName);
                    }

                    //noinspection ResultOfMethodCallIgnored
                    targetFile.getParentFile().mkdirs();
                    if (!HttpUtil.get(url, targetFile, true, true)) {
                        throw new RuntimeException("package.xml: Can't download '" + url + "'.");
                    }
                }

                if (localName.equals("copy")) {
                    String from = AttributeUtil.getValue(attributes, "from");
                    String to = AttributeUtil.getValue(attributes, "to");

                    if (StringUtils.isBlank("from")) {
                        throw new RuntimeException("package.xml: Attribute copy:from should be non-empty.");
                    }

                    if (StringUtils.isBlank("to")) {
                        throw new RuntimeException("package.xml: Attribute copy:to should be non-empty.");
                    }

                    if (from.startsWith("./") || from.startsWith(".\\")) {
                        from = new File(getOpts().get(Option.TEMPLATE_DIR), packageName + "\\" + from.substring(2)).getAbsolutePath();
                    }

                    File sourceFile = new File(from);
                    File targetFile = new File(templateVersionDir, to);

                    try {
                        if (sourceFile.isFile()) {
                            if (to.endsWith("\\") || to.endsWith("/")) {
                                //noinspection ResultOfMethodCallIgnored
                                targetFile.mkdirs();
                                FileUtils.copyFileToDirectory(sourceFile, targetFile);
                            } else {
                                //noinspection ResultOfMethodCallIgnored
                                targetFile.getParentFile().mkdirs();
                                FileUtils.copyFile(sourceFile, targetFile);
                            }
                        } else {
                            //noinspection ResultOfMethodCallIgnored
                            targetFile.mkdirs();
                            FileUtils.copyDirectory(sourceFile, targetFile);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Can't copy '" + sourceFile + "' to '" + targetFile + "'.", e);
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                if (localName.equals("tags")) {
                    for (String item : content.split("[,;]+")) {
                        String tag = item.trim();
                        if (!tag.isEmpty()) {
                            tags.add(prepareTag(tag));
                        }
                    }
                }

                if (localName.equals("description")) {
                    getOpts().put(".description.pbox", content);
                }

                if (localName.equals("iconUrl")) {
                    getOpts().put(".iconUrl.pbox", content);
                }

                if (localName.equals("authors")) {
                    getOpts().put(".authors.pbox", content);
                }
            }
        });

        if (StringUtils.isNoneBlank(getOpts().get(".authors.pbox"))) {
            try {
                FileUtils.write(new File(outputVersionDir.getParent(), ".authors.pbox"), getOpts().get(".authors.pbox").trim());
            } catch (IOException e) {
                throw new RuntimeException("Can't write authors file to " + new File(outputVersionDir.getParent(), ".authors.pbox") + ".", e);
            }
        }

        if (StringUtils.isNoneBlank(getOpts().get(".iconUrl.pbox"))) {
            try {
                String fileName = "icon." + FilenameUtils.getExtension(new URL(getOpts().get(".iconUrl.pbox")).getFile());
                HttpUtil.get(getOpts().get(".iconUrl.pbox"), new File(outputVersionDir.getParent(), fileName), true, true);
            } catch (MalformedURLException e) {
                String from = getOpts().get(".iconUrl.pbox");
                if (from.startsWith("./") || from.startsWith(".\\")) {
                    from = new File(getOpts().get(Option.TEMPLATE_DIR), packageName + "\\" + from.substring(2)).getAbsolutePath();
                }
                File fromFile = new File(from);
                if (fromFile.isFile()) {
                    String fileName = "icon." + FilenameUtils.getExtension(from);
                    File iconFile = new File(outputVersionDir.getParent(), fileName);
                    try {
                        FileUtils.copyFile(
                                fromFile,
                                iconFile
                        );
                    } catch (IOException e1) {
                        throw new RuntimeException("Can't copy from '" + fromFile + "' to '" + iconFile + "'.", e1);
                    }
                } else {
                    throw new RuntimeException("Can't find icon by '" + getOpts().get(".iconUrl.pbox") + "'.");
                }
            }
        }

        if (StringUtils.isNoneBlank(getOpts().get(".description.pbox"))) {
            try {
                FileUtils.write(new File(outputVersionDir.getParent(), ".description.pbox"), getOpts().get(".description.pbox").trim());
            } catch (IOException e) {
                throw new RuntimeException("Can't write authors file to " + new File(outputVersionDir.getParent(), ".description.pbox") + ".", e);
            }
        }

        if (!tags.isEmpty()) {
            try {
                FileUtils.write(new File(outputVersionDir.getParent(), ".tags.pbox"), StringUtils.join(tags, "\n"));
            } catch (IOException e) {
                throw new RuntimeException("Can't write tags file '" + new File(outputVersionDir.getParent(), ".tags.pbox") + "'.");
            }
        }

        try {
            FileUtils.write(new File(templateVersionDir, "pbox.xml"), pboxDescriptor);
        } catch (IOException e) {
            throw new RuntimeException("Can't write '" + new File(templateVersionDir, "pbox.xml") + "'.", e);
        }

        File templatePackageFile = new File(templateVersionDir.getParentFile(), packageName + "$" + version + ".pbox.7z");
        CompressUtil.compress(templatePackageFile, templateVersionDir);

        if (templatePackageFile.isFile() && templatePackageFile.length() > 0) {
            try {
                FileUtils.copyFile(templatePackageFile, new File(outputVersionDir, templatePackageFile.getName()));
            } catch (IOException e) {
                throw new RuntimeException("Can't copy " + templatePackageFile
                        + " to " + new File(outputVersionDir, templatePackageFile.getName()) + ".", e);
            }

            try {
                FileUtils.write(new File(outputVersionDir, "pbox.xml"), pboxDescriptor);
            } catch (IOException e) {
                throw new RuntimeException("Can't write file " + new File(outputVersionDir, "pbox.xml") + ".", e);
            }

            try {
                FileUtils.write(new File(outputVersionDir.getParent(), "pbox.xml"), pboxDescriptor);
            } catch (IOException e) {
                throw new RuntimeException("Can't write file " + new File(outputVersionDir, "pbox.xml") + ".", e);
            }

            try {
                FileUtils.forceDelete(templateVersionDir);
            } catch (IOException e) {
                // No operations.
            }

            try {
                FileUtils.forceDelete(templatePackageFile);
            } catch (IOException e) {
                // No operations.
            }

            logger.info("Package has been built, directory='" + outputVersionDir + "'.");
        } else {
            throw new RuntimeException("Can't find '" + templatePackageFile + "' after compression of '" + templateVersionDir + "'.");
        }
    }

    private String prepareTag(String tag) {
        return tag.trim().replaceAll("\\s+", "").toLowerCase();
    }

    private String readFile(String templatesDir, String packageName, String fileName) {
        File descriptorFile = new File(templatesDir, packageName + "\\" + fileName);

        if (!descriptorFile.isFile() || descriptorFile.length() == 0) {
            throw new RuntimeException("Can't find '" + descriptorFile + "'.");
        }

        String descriptor;
        try {
            descriptor = FileUtils.readFileToString(descriptorFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't read '" + descriptorFile + "'.", e);
        }

        for (String key : getOpts().getKeys()) {
            descriptor = descriptor.replaceAll("!" + key + "!", getOpts().get(key));
        }

        for (String key : getOpts().getKeys()) {
            descriptor = descriptor.replaceAll("!" + key + "!", getOpts().get(key));
        }

        return descriptor;
    }
}
