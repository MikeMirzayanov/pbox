package me.pbox.pkg;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Pkg {
    private final String name;
    private String version;
    private String title;

    public Pkg(String string) {
        if (!string.matches("[-a-z_0-9]{1,80}(\\$[-a-z_0-9]{1,80})?")) {
            throw new IllegalArgumentException("Invalid package '" + string + "'.");
        }

        if (string.contains("$")) {
            int dollarIndex = string.lastIndexOf('$');
            name = string.substring(0, dollarIndex);
            version = string.substring(dollarIndex + 1);
        } else {
            name = string;
        }
    }

    public Pkg(String version, String name) {
        this.version = version;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLatestVersion() {
        setVersion(PkgUtil.findLatestVersion(this));
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("version", version);
        map.put("title", title);
        return map;
    }

    @Override
    public String toString() {
        if (version == null) {
            return name;
        } else {
            return name + '$' + version;
        }
    }
}
