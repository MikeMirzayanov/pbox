/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.captions.dao.impl;

import com.codeforces.commons.text.StringUtil;
import com.google.inject.Singleton;
import freemarker.cache.SoftCacheStorage;
import freemarker.template.Configuration;
import me.pbox.site.captions.TemplateEngineConfigurations;
import me.pbox.site.captions.dao.CaptionDao;
import me.pbox.site.captions.database.CaptionDataSource;
import me.pbox.site.captions.model.Caption;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.jacuzzi.core.DatabaseException;
import org.jacuzzi.core.GenericDaoImpl;
import org.nocturne.main.ApplicationContext;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

@Singleton
public class CaptionDaoImpl extends GenericDaoImpl<Caption, Long> implements CaptionDao {
    private static final ConcurrentMap<String, Caption> cache = new ConcurrentHashMap<>();
    private static final Semaphore semaphore = new Semaphore(4, true);
    private static final Logger logger = Logger.getLogger(CaptionDaoImpl.class);

    CaptionDaoImpl() {
        super(CaptionDataSource.getInstance());
        addAll();
    }

    private void addAll() {
        List<Caption> captions = findAll();
        for (Caption caption : captions) {
            String cacheKey = caption.getShortcutSha1() + (char) 1 + caption.getLocale();
            cache.put(cacheKey, caption);
        }
    }

    @Override
    public String shaHex(String s) {
        if (s == null) {
            return null;
        }

        boolean ascii = true;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c < 0 || c > 127) {
                ascii = false;
                break;
            }
        }

        if (ascii) {
            return DigestUtils.sha1Hex(s);
        } else {
            return shaHexUsingDatabase(s);
        }
    }

    public String shaHexUsingDatabase(String s) {
        return getJacuzzi().findString("SELECT CONVERT(SHA1(?) USING utf8)", s);
    }

    @Override
    @SuppressWarnings({"UnnecessaryBoxing"})
    public Caption find(long id) {
        return find(Long.valueOf(id));
    }

    @Override
    public Caption find(String shortcutSha1, String locale) {
        String cacheKey = shortcutSha1 + (char) 1 + locale;
        Caption cachedCaption = cache.get(cacheKey);
        if (cachedCaption != null) {
            return cachedCaption;
        }

        try {
            semaphore.acquire();
            try {
                List<Caption> captions = findBy("shortcutSha1 = ? AND locale = ?", shortcutSha1, locale);
                if (captions.isEmpty()) {
                    return null;
                } else {
                    if (captions.size() == 1) {
                        Caption caption = captions.get(0);
                        cache.put(cacheKey, caption);
                        logger.warn("Caption cache missed " + caption.getShortcut() + '(' + caption.getLocale() + ')');
                        return caption;
                    } else {
                        throw new DatabaseException("Too many captions with shortcutSha1 = \""
                                + shortcutSha1 + "\" and locale = \"" + locale + "\".");
                    }
                }

            } finally {
                semaphore.release();
            }
        } catch (InterruptedException ignored) {
            return null;
        }
    }

    @Override
    public void save(Caption object) {
        if (!StringUtil.isEmpty(object.getShortcut())) {
            object.setShortcutSha1(getJacuzzi().findString("SELECT CONVERT(SHA1(?) USING utf8)",
                    object.getShortcut()));
        }

        Caption prev = find(object.getShortcutSha1(), object.getLocale());

        if (prev != null) {
            object.setId(prev.getId());
            object.setShortcut(prev.getShortcut());
        }

        if (prev == null) {
            prev = find(object.getShortcutSha1(), ApplicationContext.getInstance().getDefaultLocale().toString());

            if (prev != null) {
                object.setShortcut(prev.getShortcut());
            }
        }

        clearCache();
        super.save(object);
    }


    @Override
    public void clearCache() {
        cache.clear();

        if (ApplicationContext.getInstance().isDebug()) {
            for (Configuration configuration : TemplateEngineConfigurations.get()) {
                configuration.clearEncodingMap();
                configuration.clearSharedVariables();
                configuration.clearTemplateCache();
                configuration.setCacheStorage(new SoftCacheStorage());
            }
        }

        logger.warn("Captions cache has been cleared.");

        addAll();
    }

    @Override
    public void deleteByShortcutSha1(String shortcutSha1) {
        getJacuzzi().execute("DELETE FROM Caption WHERE shortcutSha1 = ?", shortcutSha1);
        clearCache();
    }

    @Override
    public List<Caption> findByShortcutSha1(String shortcutSha1) {
        return findBy("shortcutSha1 = ?", shortcutSha1);
    }

    @Override
    public List<Caption> findByValue(String value) {
        return findBy("value = ?", value);
    }

    @Override
    public void insert(Caption caption) {
        caption.setShortcutSha1(shaHex(caption.getShortcut()));

        try {
            super.insert(caption);
            clearCache();
        } catch (DatabaseException e) {
            throw new DatabaseException("Can't insert caption with shortcut=\"" +
                    caption.getShortcut() + "\".", e);
        }
    }
}
