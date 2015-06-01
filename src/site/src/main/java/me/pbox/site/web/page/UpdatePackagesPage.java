package me.pbox.site.web.page;


import com.codeforces.commons.properties.PropertiesUtil;
import com.google.inject.Inject;
import me.pbox.site.dao.PackageDao;
import me.pbox.site.index.Index;
import me.pbox.site.model.Package;
import me.pbox.site.service.PackageService;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.nocturne.link.Link;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
@Link("updatePackages")
public class UpdatePackagesPage extends WebPage {
    private static final Logger logger = Logger.getLogger(UpdatePackagesPage.class);
    private static final File PACKAGES_DIR
            = new File(PropertiesUtil.getPropertyQuietly("packages-dir-path", null, "/application.properties"));

    @Inject
    private PackageDao packageDao;

    @Inject
    private PackageService packageService;

    @Inject
    private Index index;

    @Override
    public void action() {
        if (getRequest().getMethod().equalsIgnoreCase("POST")) {
            try {
                List<Package> packages = packageService.rescanPackagesDir(PACKAGES_DIR);

                for (Package p : packages) {
                    packageDao.insertOrUpdate(p);
                }
                index.insertOrUpdate(packages);

                List<String> lines = new ArrayList<>(packages.size());
                for (Package p : packages) {
                    lines.add(p.toJson());
                }
                FileUtils.writeLines(new File(PACKAGES_DIR, "packages.json"), lines);

                logger.info("Packages have been updated successfully.");
            } catch (IOException e) {
                throw new RuntimeException("Can't rescan packages.", e);
            }
        }
    }

    @Override
    public String getTitle() {
        return "DownloadListenerPage";
    }
}
