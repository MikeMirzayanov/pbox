/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.util;

import com.codeforces.commons.text.StringUtil;
import me.pbox.site.exception.ApplicationException;
import me.pbox.site.model.Localized;
import org.nocturne.main.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleUtil {
    private static final Locale defaultLocale = new Locale("ru");
    private static final String[] VALID_CODES = {"ru", "en"};

    private static final Map<String, Locale> locales = new HashMap<>(2);

    public static Locale getEnglishLocale() {
        return getLocale("en");
    }

    public static Locale getRussianLocale() {
        return getLocale("ru");
    }

    public static Locale getLocaleByName(String name) {
        name = name.toLowerCase();

        if (getEnglishLocale().getDisplayName().toLowerCase().equals(name)) {
            return getEnglishLocale();
        }

        if (getRussianLocale().getDisplayName().toLowerCase().equals(name)) {
            return getRussianLocale();
        }

        throw new IllegalArgumentException("Unknown locale display name: " + name);
    }

    public static Locale getLocale(String code) {
        if (Arrays.asList(VALID_CODES).contains(code)) {
            if (!locales.containsKey(code)) {
                locales.put(code, new Locale(code.toLowerCase()));
            }
            return locales.get(code);
        } else {
            throw new ApplicationException("Illegal language code: " + code + '.');
        }
    }

    public static boolean isValidCode(String code) {
        return Arrays.asList(VALID_CODES).contains(code);
    }

    public static Locale getDefaultLocale() {
        return defaultLocale;
    }

    public static String getCode(Locale locale) {
        return locale.getLanguage();
    }

    public static String getOtherLocale(String code) {
        for (int i = 0; i < VALID_CODES.length; i++) {
            if (VALID_CODES[i].equals(code)) {
                return VALID_CODES[(i + 1) % VALID_CODES.length];
            }
        }
        return null;
    }

    public static String getLocalizedLink(String link, String code) {
        String appendix = "locale=" + code;
        link = link.replaceAll("locale=[\\w]{2,2}", appendix);

        if (!link.contains("locale=")) {
            if (link.contains("?")) {
                link += '&' + appendix;
            } else {
                link += '?' + appendix;
            }
        }

        return link;
    }

    public static boolean isRussian(Locale locale) {
        return isRussian(getCode(locale));
    }

    public static boolean isRussian(String localeCode) {
        return "ru".equals(localeCode);
    }

    public static boolean isEnglish(Locale locale) {
        return isEnglish(getCode(locale));
    }

    public static boolean isEnglish(String localeCode) {
        return "en".equals(localeCode);
    }

    public static String getLocalizedName(Localized localized, Locale locale) {
        if (isRussian(locale)) {
            return StringUtil.isBlank(localized.getRussianName())
                    ? localized.getEnglishName()
                    : localized.getRussianName();
        }

        if (isEnglish(locale)) {
            return StringUtil.isBlank(localized.getEnglishName())
                    ? localized.getRussianName()
                    : localized.getEnglishName();
        }

        throw new ApplicationException("Expected russian or english locale.");
    }

    public static String getLocalizedName(Localized localized) {
        return getLocalizedName(localized, ApplicationContext.getInstance().getLocale());
    }

    public static String getLocalized(final String englishText, final String russianText) {
        return getLocalizedName(new Localized() {
            @Override
            public String getEnglishName() {
                return englishText;
            }

            @Override
            public String getRussianName() {
                return russianText;
            }
        });
    }

    /**
     * This method is similar to getLocalized(final String englishText, final String russianText),
     * but returns empty string if locale is english and there is no english text.
     *
     * @param englishText English version of text
     * @param russianText Russian version of text
     */
    public static String getReasonableLocalized(String englishText, String russianText) {
        Locale locale = ApplicationContext.getInstance().getLocale();

        if (isRussian(locale)) {
            return StringUtil.isBlank(russianText) ? englishText : russianText;
        }

        if (isEnglish(locale)) {
            return StringUtil.isBlank(englishText) ? "" : englishText;
        }

        throw new ApplicationException("Expected russian or english locale.");
    }

    static {
        locales.put("ru", defaultLocale);
    }
}
