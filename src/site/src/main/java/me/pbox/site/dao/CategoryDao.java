package me.pbox.site.dao;

import me.pbox.site.model.Category;
import me.pbox.site.model.Package;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public interface CategoryDao {
    Category find(long id);
    Category find(String name);
    void ensureExistsByPackage(Package p);
    void onDownload(Package p);
}
