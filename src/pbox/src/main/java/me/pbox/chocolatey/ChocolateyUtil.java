package me.pbox.chocolatey;

import me.pbox.compress.CompressUtil;
import me.pbox.http.HttpUtil;
import me.pbox.xml.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ChocolateyUtil {
    public static ChocolateyUtil.Package grub(String packageName) throws IOException {
        String url = "https://chocolatey.org/packages/" + packageName;
        File informationFile = HttpUtil.getTemporaryFile(url, true, true);
        String information = FileUtils.readFileToString(informationFile);

        String name = getPackageName(information);
        if (name == null) {
            throw new RuntimeException("Can't find chocolatey package '" + name + "' (or at least to find package name).");
        }

        Package pack = new Package(name);
        setupPackage(pack, information);

        return pack;
    }

    private static void setupPackage(Package pack, String information) throws IOException {
        Pattern pattern = Pattern.compile("<a href=\"/packages/" + pack.getName() + "/([-_.\\w]+)\">[^<]+</a>");
        String nupkgFileUrl = null;

        for (String line : information.split("[\r\n]+")) {
            String s = line.trim();

            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                String version = matcher.group(1);
                if (!pack.getVersions().contains(version)) {
                    pack.getVersions().add(version);
                }
            }

            if (s.contains("Download the raw nupkg file")) {
                s = s.replaceAll("[\"\'=<>]", " ");
                Scanner scanner = new Scanner(s);
                while (scanner.hasNext()) {
                    String token = scanner.next();
                    if (token.startsWith("http:") || token.startsWith("https:")) {
                        if (nupkgFileUrl == null) {
                            nupkgFileUrl = token;
                        }
                        int pos = token.lastIndexOf('/');
                        pack.getVersions().add(token.substring(pos + 1));
                    }
                }
            }
        }

        if (pack.getVersions().isEmpty()) {
            return;
        }

        File nupkgFile = HttpUtil.getTemporaryFile(nupkgFileUrl, true, true);
        if (nupkgFile == null || !nupkgFile.isFile() || nupkgFile.length() == 0) {
            throw new RuntimeException("Can't find chocolatey file " + nupkgFileUrl + ".");
        }

        File nupkgDir = new File(nupkgFile.getParent(), pack.getName() + "@" + pack.getVersions().get(0));
        if (nupkgDir.exists()) {
            FileUtils.forceDelete(nupkgDir);
        }
        //noinspection ResultOfMethodCallIgnored
        nupkgDir.mkdirs();

        if (!CompressUtil.uncompress(nupkgFile, nupkgDir)) {
            throw new RuntimeException("Can't uncompress " + nupkgFile + ".");
        }

        File nuspecFile = new File(nupkgDir, pack.getName() + ".nuspec");
        String nuspec = FileUtils.readFileToString(nuspecFile);
        nuspec = nuspec.replaceAll("xmlns=\"http://schemas.microsoft.com/packaging/\\d+/\\d+/nuspec.xsd\"", "");

        pack.setDescription(XmlUtil.extractFromXml(new ByteArrayInputStream(nuspec.getBytes()), "//description", String.class));
        pack.setIconUrl(XmlUtil.extractFromXml(new ByteArrayInputStream(nuspec.getBytes()), "//iconUrl", String.class));
        pack.setAuthors(XmlUtil.extractFromXml(new ByteArrayInputStream(nuspec.getBytes()), "//authors", String.class));

        String tags = XmlUtil.extractFromXml(new ByteArrayInputStream(nuspec.getBytes()), "//tags", String.class).toLowerCase();
        if (StringUtils.isNoneBlank(tags)) {
            pack.setTags(Arrays.asList(tags.split("[\r\n\\s]+")));
        }

        File installFile = new File(nupkgDir, "tools\\chocolateyInstall.ps1");
        String install = FileUtils.readFileToString(installFile);
        List<String> urls = new ArrayList<>();

        for (String s : install.split("[\"\'\\s]+")) {
            String word = s.trim();

            if (word.startsWith("http://") || word.startsWith("https://")) {
                if (word.contains(".msi") || word.contains(".exe")) {
                    urls.add(word);
                }
            }
        }

        Collections.sort(urls, new Comparator<String>() {
            int type(String o) {
                if (o.contains("x64")) {
                    return 2;
                }

                if (o.contains("x86") || o.contains("x32")) {
                    return -2;
                }

                if (o.contains("64") && (o.contains("32") || o.contains("86"))) {
                    return 0;
                }

                if (o.contains("64")) {
                    return -1;
                }

                if (o.contains("32") || o.contains("86")) {
                    return 1;
                }

                return 0;
            }

            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(type(o1), type(o2));
            }
        });

        pack.getUrls().addAll(urls);
    }

    private static String getPackageName(String information) {
        Pattern pattern = Pattern.compile("[^/]*/packages/([-_.\\w]+)/([-_.\\w]+)/ContactAdmins[^/]*");

        for (String line : information.split("[\r\n]+")) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    public static final class Package {
        private final String name;
        private String description;
        private List<String> versions = new ArrayList<>();
        private String iconUrl;
        private String authors;
        private List<String> urls = new ArrayList<>();
        private List<String> tags = new ArrayList<>();

        public Package(String name) {
            this.name = name;
        }

        public String getAuthors() {
            return authors;
        }

        public void setAuthors(String authors) {
            this.authors = authors;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getVersions() {
            return versions;
        }

        public void setVersions(List<String> versions) {
            this.versions = versions;
        }

        public List<String> getUrls() {
            return urls;
        }

        public void setUrls(List<String> urls) {
            this.urls = urls;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(grub("7zip.install"));
    }
}
