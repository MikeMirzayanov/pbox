package me.pbox.site.index;

import me.pbox.site.model.Package;

import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public interface Index {
    void insertOrUpdate(List<Package> packages);
    List<Package> find(String query) throws IllegalQueryException;
    List<Package> findByName(String query) throws IllegalQueryException;
}
