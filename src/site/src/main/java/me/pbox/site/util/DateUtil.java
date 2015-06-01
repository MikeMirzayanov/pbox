/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.util;

import org.nocturne.main.ApplicationContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author alazarev
 */
public class DateUtil {
    private static final String SYSTEM_DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static final String SYSTEM_DATE_TIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    private static final String SYSTEM_TIME_FORMAT_STRING = "HH:mm:ss";

    private static final String DEFAULT_DATE_FORMAT_STRING = "MMM/dd/yyyy";
    private static final String RU_DATE_FORMAT_STRING = "dd.MM.yyyy";

    private static final String DEFAULT_DATE_TIME_FORMAT_STRING = "MMM/dd/yyyy HH:mm";
    private static final String RU_DATE_TIME_FORMAT_STRING = "dd.MM.yyyy HH:mm";

    private static final String DEFAULT_TIME_FORMAT_STRING = "HH:mm";
    private static final String RU_TIME_FORMAT_STRING = "HH:mm";

    private static final ThreadLocal<DateFormat> SYSTEM_DATE_FORMAT = newSimpleDateFormat(SYSTEM_DATE_FORMAT_STRING, LocaleUtil.getDefaultLocale());

    private static final ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = newSimpleDateFormat(DEFAULT_DATE_FORMAT_STRING, LocaleUtil.getDefaultLocale());
    private static final ThreadLocal<DateFormat> RU_DATE_FORMAT = newSimpleDateFormat(RU_DATE_FORMAT_STRING, LocaleUtil.getDefaultLocale());

    private static final ThreadLocal<DateFormat> DEFAULT_DATE_TIME_FORMAT = newSimpleDateFormat(DEFAULT_DATE_TIME_FORMAT_STRING, LocaleUtil.getDefaultLocale());
    private static final ThreadLocal<DateFormat> RU_DATE_TIME_FORMAT = newSimpleDateFormat(RU_DATE_TIME_FORMAT_STRING, LocaleUtil.getDefaultLocale());

    private static final ThreadLocal<DateFormat> DEFAULT_TIME_FORMAT = newSimpleDateFormat(DEFAULT_TIME_FORMAT_STRING, LocaleUtil.getDefaultLocale());
    private static final ThreadLocal<DateFormat> RU_TIME_FORMAT = newSimpleDateFormat(RU_TIME_FORMAT_STRING, LocaleUtil.getDefaultLocale());

    public static String getSystemDateFormatString() {
        return SYSTEM_DATE_FORMAT_STRING;
    }

    public static String getSystemDateTimeFormatString() {
        return SYSTEM_DATE_TIME_FORMAT_STRING;
    }

    public static String getSystemTimeFormatString() {
        return SYSTEM_TIME_FORMAT_STRING;
    }

    public static ThreadLocal<DateFormat> newSimpleDateFormat(final String pattern, final Locale locale) {
        return new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat(pattern, locale);
            }
        };
    }

    public static ThreadLocal<DateFormat> newSimpleDateFormat(String pattern) {
        return newSimpleDateFormat(pattern, null);
    }

    private DateUtil() {
    }

    public static String toSystemDateString(Date date) {
        return SYSTEM_DATE_FORMAT.get().format(date);
    }

    public static java.sql.Date fromSystemDateString(String stringDate) throws ParseException {
        return new java.sql.Date(SYSTEM_DATE_FORMAT.get().parse(stringDate).getTime());
    }

    public static String getDateFormatString(Locale locale) {
        if (LocaleUtil.isRussian(locale)) {
            return RU_DATE_FORMAT_STRING;
        } else {
            return DEFAULT_DATE_FORMAT_STRING;
        }
    }

    public static String getDateTimeFormatString(Locale locale) {
        if (LocaleUtil.isRussian(locale)) {
            return RU_DATE_TIME_FORMAT_STRING;
        } else {
            return DEFAULT_DATE_TIME_FORMAT_STRING;
        }
    }

    public static String getTimeFormatString(Locale locale) {
        if (LocaleUtil.isRussian(locale)) {
            return RU_TIME_FORMAT_STRING;
        } else {
            return DEFAULT_TIME_FORMAT_STRING;
        }
    }

    public static DateFormat getDateFormat(Locale locale) {
        ThreadLocal<DateFormat> format = DEFAULT_DATE_FORMAT;

        if (LocaleUtil.isRussian(locale)) {
            format = RU_DATE_FORMAT;
        }

        return format.get();
    }

    public static DateFormat getDateTimeFormat(Locale locale) {
        ThreadLocal<DateFormat> format = DEFAULT_DATE_TIME_FORMAT;

        if (LocaleUtil.isRussian(locale)) {
            format = RU_DATE_TIME_FORMAT;
        }

        return format.get();
    }

    public static DateFormat getTimeFormat(Locale locale) {
        ThreadLocal<DateFormat> format = DEFAULT_TIME_FORMAT;

        if (LocaleUtil.isRussian(locale)) {
            format = RU_TIME_FORMAT;
        }

        return format.get();
    }

    public static Date parse(String day, String time) throws ParseException {
        String localeCode = LocaleUtil.getCode(ApplicationContext.getInstance().getLocale());
        return parse(day, time, localeCode);
    }

    private static Date parse(String day, String time, String code) throws ParseException {
        String datetime = day.trim() + ' ' + time.trim();
        try {
            return getDateTimeFormat(LocaleUtil.getLocale(code)).parse(datetime);
        } catch (ParseException ignored) {
            return getDateTimeFormat(LocaleUtil.getLocale(LocaleUtil.getOtherLocale(code))).parse(datetime);
        }
    }

    public static Date add(Date time, int calendarField, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(time);
        calendar.add(calendarField, amount);
        return calendar.getTime();
    }

    public static String getDateTimeFormatted(Date date, Locale locale) {
        return getDateTimeFormat(locale).format(date);
    }
}
