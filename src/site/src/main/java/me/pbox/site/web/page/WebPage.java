package me.pbox.site.web.page;

import com.google.inject.Inject;
import me.pbox.site.util.LocaleUtil;
import me.pbox.site.web.frame.FooterFrame;
import me.pbox.site.web.frame.HeaderFrame;
import org.nocturne.main.ApplicationContext;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public abstract class WebPage extends ApplicationPage {
    @Inject
    private HeaderFrame headerFrame;

    @Inject
    private FooterFrame footerFrame;

    public abstract String getTitle();

    @Override
    public void initializeAction() {
        super.initializeAction();

        put("pageTitle", getTitle());
        putGlobal("locale", LocaleUtil.getCode(ApplicationContext.getInstance().getLocale()));

        boolean debug = ApplicationContext.getInstance().isDebug();
        putGlobal("debug", debug);
    }

    @Override
    public void finalizeAction() {
        parse("headerFrame", headerFrame);
        parse("footerFrame", footerFrame);

        super.finalizeAction();

    }
}
