package me.pbox.site.dao;

import me.pbox.site.model.Package;

import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public interface PackageDao {
    Package find(long id);
    Package find(String name, String version);
    Package insertOrUpdate(Package p);
    List<Package> findAll();
    void onDownload(Package p);
}
