package me.pbox.site.dao.impl;

import me.pbox.site.dao.CategoryDao;
import me.pbox.site.model.Category;
import me.pbox.site.model.Package;
import org.jacuzzi.core.DatabaseException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class CategoryDaoImpl extends ApplicationDaoImpl<Category> implements CategoryDao {
    @Override
    public Category find(String name) {
        return findOnlyBy("name=?", name);
    }

    @Override
    public void ensureExistsByPackage(Package p) {
        Category category = find(p.getName());
        if (category == null) {
            category = new Category();
            category.setName(p.getName());
            try {
                insert(category);
            } catch (DatabaseException ignored) {
                // No operations.
            }
        }
    }

    @Override
    public void onDownload(Package p) {
        if (getJacuzzi().execute("UPDATE `Category` SET `downloadCount`=`downloadCount`+1 WHERE `name`=?", p.getName()) != 1) {
            throw new DatabaseException("Can't update category by " + p + '.');
        }
    }
}
