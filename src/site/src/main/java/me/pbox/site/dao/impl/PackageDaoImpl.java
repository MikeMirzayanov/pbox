package me.pbox.site.dao.impl;

import com.google.inject.Inject;
import me.pbox.site.dao.CategoryDao;
import me.pbox.site.dao.PackageDao;
import me.pbox.site.model.Package;
import org.jacuzzi.core.DatabaseException;

import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class PackageDaoImpl extends ApplicationDaoImpl<Package> implements PackageDao {
    @Inject
    private CategoryDao categoryDao;

    @Override
    public Package find(String name, String version) {
        return findOnlyBy("name=? AND version=?", name, version);
    }

    @Override
    public Package insertOrUpdate(Package p) {
        Package before = find(p.getName(), p.getVersion());
        if (before == null) {
            p.setCreationTime(findNow());
            insert(p);
        } else {
            p.setId(before.getId());
            p.setCreationTime(before.getCreationTime());
            p.setUserId(p.getUserId());
            update(p);
        }

        return p;
    }

    @Override
    public List<Package> findAll() {
        return findBy("1 ORDER BY creationTime DESC, id DESC");
    }

    @Override
    public void onDownload(Package p) {
        categoryDao.ensureExistsByPackage(p);

        if (getJacuzzi().execute("UPDATE `Package` SET `downloadCount`=`downloadCount`+1 WHERE `name`=?", p.getName()) != 1) {
            throw new DatabaseException("Can't update " + p + '.');
        }

        categoryDao.onDownload(p);
    }
}
