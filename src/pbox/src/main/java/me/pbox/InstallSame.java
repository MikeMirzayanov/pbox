package me.pbox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InstallSame {
    private static void run(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                processPackageDir(file);
            }
        }
    }

    private static void processPackageDir(File dir) {
        String name = dir.getName();
        String version = null;
        String arch = null;
        File pboxFile = new File(dir, "." + name + ".pbox");
        if (pboxFile.exists()) {
            try {
                String[] lines = readFileToString(pboxFile).split("[\n\r]+");
                for (String line : lines) {
                    line = line.trim();
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        if ("version".equals(key)) {
                            version = value;
                        } else if ("arch".equals(key)) {
                            arch = value;
                        }
                    }
                }

                if (version != null && arch != null) {
                    System.out.println("pbox install " + name + " --version=" + version + " --arch=" + arch);
                }
            } catch (IOException e) {
                // No operations.
            }
        }
    }

    /**
     * Reads the contents of a file into a string.
     *
     * @param file the file to read
     * @return the contents of the file as a string
     */
    private static String readFileToString(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            run(new File("."));
        } else if (args.length == 1) {
            run(new File(args[0]));
        } else {
            System.err.println("Usage: java -cp pbox.jar me.pbox.InstallSame [directory]");
            System.exit(1);
        }
    }
}
