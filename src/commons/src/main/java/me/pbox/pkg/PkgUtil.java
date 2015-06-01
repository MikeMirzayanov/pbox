package me.pbox.pkg;

import me.pbox.env.Environment;
import me.pbox.env.Sources;
import me.pbox.http.HttpUtil;
import me.pbox.xml.XmlUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class PkgUtil {
    public static String getPboxXmlUrl(String source, Pkg pkg) {
        return String.format("%s/%s/%s/pbox.xml",
                source, Environment.getVersion(), pkg.getName());
    }

    public static String getPbox7zUrl(String source, Pkg pkg) {
        return String.format("%s/%s/%s/%s/%s.pbox.7z",
                source, Environment.getVersion(), pkg.getName(), pkg.getVersion(), pkg);
    }

    public static File findPboxXmlFile(Pkg pkg) {
        for (String source : Sources.getList()) {
            String url = getPboxXmlUrl(source, pkg);

            File result = HttpUtil.getTemporaryFile(url);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public static File findPbox7zFile(Pkg pkg) {
        for (String source : Sources.getList()) {
            String url = getPbox7zUrl(source, pkg);

            File result = HttpUtil.getTemporaryFile(url);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public static String findLatestVersion(Pkg pkg) {
        for (String source : Sources.getList()) {
            String url = getPboxXmlUrl(source, pkg);

            File pboxXmlFile = HttpUtil.getTemporaryFile(url);
            if (pboxXmlFile != null) {
                try {
                    return XmlUtil.extractFromXml(pboxXmlFile, "/pbox/version", String.class);
                } catch (IOException e) {
                    throw new RuntimeException("Can't find `version` in " + pboxXmlFile + ".");
                }
            }
        }

        return null;
    }
}
