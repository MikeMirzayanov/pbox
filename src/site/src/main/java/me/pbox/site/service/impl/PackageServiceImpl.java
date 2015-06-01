package me.pbox.site.service.impl;

import com.codeforces.commons.xml.XmlUtil;
import me.pbox.site.model.Package;
import me.pbox.site.service.PackageService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class PackageServiceImpl implements PackageService {
    @Override
    public Package construct(File versionDirectory) throws IOException {
        Package p = new Package();
        String packageName = versionDirectory.getParentFile().getName();

        File descriptorFile = new File(versionDirectory, Package.DESCRIPTOR_FILENAME);
        p.setDescriptor(FileUtils.readFileToString(descriptorFile));

        File authorsFile = new File(versionDirectory.getParentFile(), Package.AUTHORS_FILENAME);
        p.setAuthors(FileUtils.readFileToString(authorsFile));

        String version = XmlUtil.extractFromXml(descriptorFile, "/pbox/version", String.class);
        p.setVersion(version);
        p.setSizeKilobytes((int) (new File(versionDirectory, packageName + "$" + version + ".pbox.7z").length() / 1024));

        p.setTitle(XmlUtil.extractFromXml(descriptorFile, "/pbox/title", String.class));
        p.setDescription(FileUtils.readFileToString(new File(descriptorFile.getParentFile().getParentFile(),
                Package.DESCRIPTION_FILENAME)));
        p.setArchs(XmlUtil.extractFromXml(descriptorFile, "/pbox/archs", String.class));

        StringBuilder tags = new StringBuilder();
        for (String tag : FileUtils.readFileToString(new File(versionDirectory.getParentFile(),
                Package.TAGS_FILENAME)).split("[\r\n]+")) {
            if (StringUtils.isNotBlank(tag)) {
                if (tags.length() > 0) {
                    tags.append(",");
                }
                tags.append(tag);
            }
        }
        p.setTags(tags.toString());

        NodeList groupNodeList = XmlUtil.extractFromXml(descriptorFile, "/pbox/group", NodeList.class);
        StringBuilder groups = new StringBuilder();
        for (int i = 0; i < groupNodeList.getLength(); i++) {
            if (groups.length() > 0) {
                groups.append(",");
            }
            groups.append(groupNodeList.item(i).getTextContent());
        }
        p.setGroups(groups.toString());

        String[] iconUrlExts = {"png", "jpg", "ico", "gif", "svg"};
        File iconFile = null;
        for (String iconUrlExt : iconUrlExts) {
            File file = new File(versionDirectory.getParentFile(), "icon." + iconUrlExt);
            if (file.isFile()) {
                iconFile = file;
            }
        }
        if (iconFile == null) {
            throw new IOException("Can't find icon file for version directory '" + versionDirectory + "'.");
        }
        p.setIconUrl(String.format(Package.ICON_URL_PATTERN, packageName, iconFile.getName()));

        p.setName(packageName);

        StringBuilder versions = new StringBuilder();
        File[] versionDirs = versionDirectory.getParentFile().listFiles();
        if (versionDirs != null) {
            for (File dir : versionDirs) {
                if (dir.isDirectory() && new File(dir, Package.DESCRIPTOR_FILENAME).isFile()) {
                    if (versions.length() > 0) {
                        versions.append(",");
                    }
                    versions.append(dir.getName());
                }
            }
        }

        p.setVersions(versions.toString());
        p.setCreationTime(new Date(descriptorFile.lastModified()));

        return p;
    }

    @Override
    public List<Package> rescanPackagesDir(File packagesDir) throws IOException {
        List<Package> packages = new ArrayList<>();

        File[] packageDirs = packagesDir.listFiles();
        if (packageDirs != null) {
            for (File packageDir : packageDirs) {
                if (packageDir.isDirectory() && new File(packageDir, Package.DESCRIPTOR_FILENAME).isFile()) {
                    File[] versionDirs = packageDir.listFiles();
                    if (versionDirs != null) {
                        for (File versionDir : versionDirs) {
                            if (versionDir.isDirectory() && new File(versionDir, Package.DESCRIPTOR_FILENAME).isFile()) {
                                packages.add(construct(versionDir));
                            }
                        }
                    }
                }
            }
        }

        return packages;
    }
}
