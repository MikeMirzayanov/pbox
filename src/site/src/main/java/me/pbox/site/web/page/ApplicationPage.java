package me.pbox.site.web.page;

import com.google.inject.Inject;
import me.pbox.site.ApplicationListener;
import me.pbox.site.captions.TemplateEngineConfigurations;
import me.pbox.site.directive.LocalizedNameDirective;
import me.pbox.site.exception.ApplicationException;
import me.pbox.site.misc.Format;
import me.pbox.site.util.DateUtil;
import org.nocturne.link.Links;
import org.nocturne.main.ApplicationContext;
import org.nocturne.main.Page;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public abstract class ApplicationPage extends Page {
    @Inject
    private Format format;

    @Inject
    private LocalizedNameDirective localizedNameDirective;

    @SuppressWarnings("RefusedBequest")
    @Override
    public PrintWriter getWriter() {
        try {
            return new PrintWriter(new OutputStreamWriter(getOutputStream(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationException("Can't get writer for page.", e);
        }
    }

    @Override
    public void initializeAction() {
        super.initializeAction();

        putSession("startPageRenderTime", System.currentTimeMillis());

        String requestUrl = getRequest().getRequestURL().toString();
        String home = requestUrl.substring(0, requestUrl.length() - getRequest().getRequestURI().length());
        String root = home + getRequest().getContextPath();

        ApplicationListener.setUpdatePackagesUrl(home + Links.getLink(UpdatePackagesPage.class));
        putGlobal("root", root);
        putGlobal("static", root);
        putGlobal("format", format);
        putGlobal("locale", ApplicationContext.getInstance().getLocale().toString());

        if (getTemplate() != null) {
            getTemplate().setLocale(ApplicationContext.getInstance().getLocale());
            getTemplate().getConfiguration().setNumberFormat("0.######");

            getTemplate().getConfiguration().setDateFormat(DateUtil.getSystemDateFormatString());
            getTemplate().getConfiguration().setDateTimeFormat(DateUtil.getSystemDateTimeFormatString());
            getTemplate().getConfiguration().setTimeFormat(DateUtil.getSystemTimeFormatString());

            TemplateEngineConfigurations.add(getTemplate().getConfiguration());
        }

        putGlobal("localizedName", localizedNameDirective);
    }
}
