package me.pbox.site.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public interface PackageService {
    me.pbox.site.model.Package construct(File versionDirectory) throws IOException;
    List<me.pbox.site.model.Package> rescanPackagesDir(File packagesDir) throws IOException;
}
