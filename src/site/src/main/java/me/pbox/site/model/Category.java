package me.pbox.site.model;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Category extends ApplicationEntity {
    private String name;
    private int downloadCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", downloadCount=" + downloadCount +
                '}';
    }
}
