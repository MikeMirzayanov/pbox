package me.pbox.site.model;

import com.codeforces.commons.properties.PropertiesUtil;
import me.pbox.site.util.JsonUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Package extends ApplicationEntity {
    public static final String DESCRIPTOR_FILENAME = "pbox.xml";
    public static final String DESCRIPTION_FILENAME = ".description.pbox";
    public static final String AUTHORS_FILENAME = ".authors.pbox";
    public static final String TAGS_FILENAME = ".tags.pbox";
    public static final String ICON_URL_PATTERN = PropertiesUtil.getProperty("icon-url-pattern", "", "/application.properties");

    private String name;
    private String title;
    private String version;
    private String versions;
    private String authors;
    private String tags;
    private String iconUrl;
    private String description;
    private String archs;
    private String groups;
    private String descriptor;
    private long userId;
    private int downloadCount;
    private int sizeKilobytes;
    private Date creationTime;

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArchs() {
        return archs;
    }

    public void setArchs(String archs) {
        this.archs = archs;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public int getSizeKilobytes() {
        return sizeKilobytes;
    }

    public void setSizeKilobytes(int sizeKilobytes) {
        this.sizeKilobytes = sizeKilobytes;
    }

    public Date getCreationTime() {
        return creationTime == null ? null : new Date(creationTime.getTime());
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String toJson() {
        return JsonUtil.fromMap(toMap());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("title", title);
        map.put("version", version);
        map.put("versions", versions);
        map.put("authors", authors);
        map.put("tags", tags);
        map.put("iconUrl", iconUrl);
        map.put("description", description);
        map.put("archs", archs);
        map.put("groups", groups);
        map.put("descriptor", descriptor);
        map.put("downloadCount", downloadCount);
        map.put("sizeKilobytes", sizeKilobytes);
        map.put("creationTime", creationTime.toString());

        return map;
    }

    public static int compareVersions(String versionA, String versionB) {
        if (versionB.startsWith(versionA)
                && versionB.length() > versionA.length()
                && versionB.charAt(versionA.length()) == '.') {
            return -1;
        }

        if (versionA.startsWith(versionB)
                && versionA.length() > versionB.length()
                && versionA.charAt(versionB.length()) == '.') {
            return +1;
        }

        String[] tokensA = versionA.split("\\.");
        int[] subversionA = new int[tokensA.length];
        for (int i = 0; i < tokensA.length; i++) {
            try {
                subversionA[i] = Integer.parseInt(tokensA[i]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        String[] tokensB = versionB.split("\\.");
        int[] subversionB = new int[tokensB.length];
        for (int i = 0; i < tokensB.length; i++) {
            try {
                subversionB[i] = Integer.parseInt(tokensB[i]);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        for (int i = 0; i < Math.min(subversionA.length, subversionB.length); i++) {
            if (subversionA[i] < subversionB[i]) {
                return -1;
            }
            if (subversionA[i] > subversionB[i]) {
                return +1;
            }
        }

        if (subversionA.length < subversionB.length) {
            return -1;
        }

        if (subversionA.length > subversionB.length) {
            return +1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Package{" +
                "id='" + getId() + '\'' +
                ", authors='" + authors + '\'' +
                ", tags='" + tags + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", archs='" + archs + '\'' +
                ", groups='" + groups + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}
