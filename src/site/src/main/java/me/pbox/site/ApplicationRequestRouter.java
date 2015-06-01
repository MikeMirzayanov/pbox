package me.pbox.site;

import me.pbox.site.web.page.*;
import org.nocturne.link.Links;
import org.nocturne.main.LinkedRequestRouter;

/**
 * @author Mike Mirzayanov
 */
public class ApplicationRequestRouter extends LinkedRequestRouter {
    static {
        Links.add(IndexPage.class);
        Links.add(PackagesPage.class);
        Links.add(DownloadListenerPage.class);
        Links.add(UpdatePackagesPage.class);
        Links.add(HelpPage.class);
    }
}
