package me.pbox.site.web.page;

import org.nocturne.link.Link;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
@Link(";home;index.html;index.htm")
public class IndexPage extends WebPage {
    @Override
    public void action() {
    }

    @Override
    public String getTitle() {
        return "PBOX";
    }
}
