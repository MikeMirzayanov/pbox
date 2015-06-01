package me.pbox.site.web.frame;

import me.pbox.site.web.page.ApplicationPage;
import me.pbox.site.web.page.WebPage;
import org.nocturne.main.Frame;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public abstract class ApplicationFrame extends Frame {
    public ApplicationPage getApplicationPage() {
        return (ApplicationPage) this.getCurrentPage();
    }

    public ApplicationPage getWebPage() {
        return (WebPage) this.getCurrentPage();
    }
}
