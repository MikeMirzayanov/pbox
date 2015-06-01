/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.captions;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import me.pbox.site.captions.dao.CaptionDao;
import me.pbox.site.captions.model.Caption;
import me.pbox.site.util.LocaleUtil;
import org.nocturne.caption.Captions;
import org.nocturne.main.ApplicationContext;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
@SuppressWarnings("OverloadedVarargsMethod")
public class CaptionsImpl implements Captions {
    @Inject
    private CaptionDao captionDao;

    @Override
    public String find(Locale locale, String shortcut, Object... args) {
        if (locale == null) {
            locale = ApplicationContext.getInstance().getLocale();
        }

        String shortcutSha1 = captionDao.shaHex(shortcut);
        Caption caption = captionDao.find(shortcutSha1, locale.toString());

        if (caption == null) {
            Locale defaultLocale = ApplicationContext.getInstance().getDefaultLocale();

            if (locale.equals(defaultLocale)) {
                caption = new Caption();
                caption.setLocale(locale.toString());
                caption.setShortcutSha1(shortcutSha1);
                caption.setShortcut(shortcut);
                caption.setValue(shortcut);

                captionDao.insert(caption);
                return formatCaption(caption, args);
            } else {
                return find(defaultLocale, shortcut, args);
            }
        } else {
            return formatCaption(caption, args);
        }
    }

    private static String formatCaption(Caption caption, Object... args) {
        return Strings.nullToEmpty(
                args.length == 0 ? caption.getValue() : MessageFormat.format(caption.getValue(), args)
        ).trim();
    }

    @SuppressWarnings("ProhibitedExceptionCaught")
    @Override
    public String find(String shortcut, Object... args) {
        try {
            return find(ApplicationContext.getInstance().getLocale(), shortcut, args);
        } catch (NullPointerException ignored) {
            return find(LocaleUtil.getDefaultLocale(), shortcut, args);
        }
    }
}
