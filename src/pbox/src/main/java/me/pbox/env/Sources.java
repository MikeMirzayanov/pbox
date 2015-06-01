package me.pbox.env;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Sources {
    public static List<String> getList() {
        List<String> sources = new ArrayList<>();

        String lines;
        try {
            lines = FileUtils.readFileToString(new File(Environment.getPboxHomeAsFile(), "sources.lst"));
            for (String line : lines.split("\\s+")) {
                if (StringUtils.isNoneBlank(line)) {
                    sources.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't get sources.lst from PBOX_HOME [PBOX_HOME=" + Environment.getPboxHomeAsFile() + "].");
        }

        return Collections.unmodifiableList(sources);
    }
}
