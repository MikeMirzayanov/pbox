package me.pbox.env;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Environment {
    private static final String SESSION = String.valueOf(RandomUtils.nextInt(10000, 99999));
    private static final String VERSION = "1.0";

    public static String getVersion() {
        return VERSION;
    }

    public static File getPboxXml(File parent) {
        return new File(parent, "pbox.xml");
    }

    public static File getPboxHomeAsFile() {
        return new File(getPboxHome());
    }

    private static String getPboxHome() {
        if (StringUtils.isNoneBlank(System.getProperty("pbox.home"))) {
            return System.getProperty("pbox.home").trim();
        }

        if (StringUtils.isNoneBlank(System.getenv("PBOX_HOME"))) {
            return System.getenv("PBOX_HOME").trim();
        }

        if (StringUtils.isNoneBlank(System.getenv("ALLUSERSPROFILE"))) {
            return System.getenv("ALLUSERSPROFILE").trim() + "\\" + "pbox";
        }

        if (StringUtils.isNoneBlank(System.getenv("SystemDrive"))) {
            return System.getenv("SystemDrive").trim() + "\\" + "pbox";
        }

        return "/pbox";
    }

    public static File getPboxBinAsFile() {
        return new File(getPboxBin());
    }

    public static String getPboxBin() {
        return getPboxHome() + "\\bin";
    }

    public static File getPboxRegistryAsFile() {
        return new File(getPboxRegistry());
    }

    public static String getPboxRegistry() {
        return getPboxHome() + "\\registry";
    }

    public static File getPboxTempAsFile() {
        return new File(getPboxTemp());
    }

    public static String getPboxTemp() {
        String temp = getPboxHome() + "\\temp\\" + SESSION;
        //noinspection ResultOfMethodCallIgnored
        new File(temp).mkdirs();
        return temp;
    }

    public static String getBin(String tool) {
        if (tool.toLowerCase().endsWith(".exe")) {
            throw new IllegalArgumentException("Expected tool name without extension, but got `" + tool + "`.");
        }

        return "\"" + getPboxBin() + "\\" + tool + ".exe\"";
    }

    static {
        InputStream resourceInputStream = Environment.class.getResourceAsStream("/bin.lst");
        try {
            String resourceList = IOUtils.toString(resourceInputStream);
            String[] resources = resourceList.split("[\r\n]+");
            for (String resource : resources) {
                if (StringUtils.isNoneBlank(resource)) {
                    //noinspection ResultOfMethodCallIgnored
                    getPboxBinAsFile().mkdirs();
                    File targetResourceFile = new File(getPboxBinAsFile(), resource);
                    if (!targetResourceFile.isFile() || targetResourceFile.length() == 0) {
                        try (InputStream inputStream = Environment.class.getResourceAsStream("/bin/" + resource)) {
                            FileOutputStream outputStream = new FileOutputStream(targetResourceFile);
                            IOUtils.copy(inputStream, outputStream);
                            outputStream.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't process resource /bin.lst.", e);
        } finally {
            IOUtils.closeQuietly(resourceInputStream);
        }
    }
}
