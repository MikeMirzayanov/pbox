package me.pbox.site.web.page;

import com.google.inject.Inject;
import me.pbox.site.dao.PackageDao;
import me.pbox.site.model.Package;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.nocturne.annotation.Parameter;
import org.nocturne.link.Link;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
@Link("onDownload/{packageName}/{version};onDownload/{packageName}/{version}/{key}")
public class DownloadListenerPage extends WebPage {
    @Parameter
    private String packageName;

    @Parameter
    private String version;

    @Parameter
    private String key;

    @Inject
    private PackageDao packageDao;

    @Override
    public void action() {
        final String sessionKeyName = packageName + "#key";

        if (StringUtils.isBlank(key)) {
            String value = RandomStringUtils.randomAlphanumeric(10);
            putSession(sessionKeyName, value);
            put("key", value);
        } else {
            String value = getSession(sessionKeyName, String.class);
            if (key.equals(value)) {
                handle();
                put("key", "OK");
            } else {
                put("key", "FAILED");
            }
            removeSession(sessionKeyName);
        }
    }

    private void handle() {
        Package p = packageDao.find(packageName, version);
        if (p != null) {
            packageDao.onDownload(p);
        }
    }

    @Override
    public String getTitle() {
        return "DownloadListenerPage";
    }
}
