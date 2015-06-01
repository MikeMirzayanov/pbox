package me.pbox.site.misc;

import com.codeforces.commons.properties.PropertiesUtil;
import com.codeforces.commons.time.TimeUtil;
import com.google.inject.Inject;
import me.pbox.site.dao.DateDao;
import me.pbox.site.util.DateUtil;
import org.nocturne.main.ApplicationContext;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author Maxim Gusarov (gusarov.maxim@gmail.com)
 */
public class Format {
    @Inject
    private DateDao dateDao;

    private static final TimeZone TIME_ZONE
            = TimeZone.getTimeZone(PropertiesUtil.getProperty("database.timezone", "Europe/Moscow", "/database.properties"));

    @SuppressWarnings({"DollarSignInName"})
    private static String $(String format, Object... args) {
        return ApplicationContext.getInstance().$(format, args);
    }

    private static ApplicationContext getApplicationContext() {
        return ApplicationContext.getInstance();
    }

    private static String internalHumanSeconds(long difference) {
        if (difference < 30) {
            return getApplicationContext().$("moment");
        }

        long minutes = (difference + 30) / 60;

        if (minutes < 2 * 60) {
            return humanNumeric("{0} minutes", minutes);
        }

        long hours = (minutes + 30) / 60;

        if (hours < 2 * 24) {
            return humanNumeric("{0} hours", hours);
        }

        long days = (hours + 12) / 24;

        if (days < 2 * 7) {
            return humanNumeric("{0} days", days);
        }

        long weeks = (days + 3) / 7;

        if (weeks < 2 * 4) {
            return humanNumeric("{0} weeks", weeks);
        }

        long months = Math.round(days / 30.417);

        if (months < 2 * 12) {
            return humanNumeric("{0} months", months);
        }

        long years = Math.round(days / 365.25);

        return humanNumeric("{0} years", years);
    }

    public static String humanNumeric(String message, long count) {
        long lastDigit = count % 10;
        long tens = (count / 10) % 10;

        if (lastDigit == 1 && tens != 1) {
            if (count == 1) {
                return ApplicationContext.getInstance().$(message + " / =1", 1);
            } else {
                return ApplicationContext.getInstance().$(message + " / 1", count);
            }
        }

        if (lastDigit > 1 && lastDigit <= 4 && tens != 1) {
            return ApplicationContext.getInstance().$(message + " / 2-4", count);
        }

        return ApplicationContext.getInstance().$(message, count);
    }

    private String internalHumanTime(Date date) {
        long difference = (findNow().getTime() - date.getTime()) / 1000L;

        if (difference < 0 && difference > -180) {
            difference = 0;
        }

        if (difference < 0) {
            DateFormat format = DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM, DateFormat.MEDIUM, getApplicationContext().getLocale()

            );
            return format.format(date);
        }

        if (difference < 30) {
            return getApplicationContext().$("moment ago");
        }

        long minutes = (difference + 30) / 60;

        if (minutes < 2 * 60) {
            return humanNumeric("{0} minutes ago", minutes);
        }

        long hours = (minutes + 30) / 60;

        if (hours < 2 * 24) {
            return humanNumeric("{0} hours ago", hours);
        }

        long days = (hours + 12) / 24;

        if (days < 2 * 7) {
            return humanNumeric("{0} days ago", days);
        }

        long weeks = (days + 3) / 7;

        if (weeks < 2 * 4) {
            return humanNumeric("{0} weeks ago", weeks);
        }

        long months = Math.round(days / 30.417);

        if (months < 2 * 12) {
            return humanNumeric("{0} months ago", months);
        }

        long years = Math.round(days / 365.25);

        return humanNumeric("{0} years ago", years);
    }

    private Date findNow() {
        return dateDao.findNow();
    }

    public String humanTime(Date date) {
        DateFormat format = DateUtil.getDateTimeFormat(ApplicationContext.getInstance().getLocale());
        String exactTime = format.format(date);
        return "<span title=\"" + exactTime + "\">" + internalHumanTime(date) + "</span>";
    }

    @SuppressWarnings("IfStatementWithIdenticalBranches")
    public String flagCode(String locale) {
        if ("ru".equalsIgnoreCase(locale)) {
            return "ru";
        }

        if ("en".equalsIgnoreCase(locale)) {
            return "gb";
        }

        return "gb";
    }

    public String formatCaption(String shortcut, String arg) {
        return getApplicationContext().$(shortcut, arg);
    }

    @SuppressWarnings("OverloadedVarargsMethod")
    public String formatCaption(String shortcut, Object... args) {
        return getApplicationContext().$(shortcut, args);
    }

    public String formatTime(Date date) {
        return DateUtil.getDateTimeFormat(ApplicationContext.getInstance().getLocale()).format(date);
    }

    public String formatDate(Date date) {
        return DateUtil.getDateFormat(ApplicationContext.getInstance().getLocale()).format(date);
    }

    public String formatTimesiteUrl(Date date) {
        Calendar calendar = new GregorianCalendar(TIME_ZONE);
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format("http://timeanddate.com/worldclock/fixedtime.html?day=%d&month=%d&year=%d&hour=%d&min=%d&sec=%d&p1=166", day, month, year, hour, minute, second);
    }

    public String formatSeconds(long seconds) {
        String stringSeconds = String.format("%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);

        if (seconds < 2 * TimeUtil.MILLIS_PER_DAY / TimeUtil.MILLIS_PER_SECOND) {
            return stringSeconds;
        } else {
            return String.format("<span title=\"%s\">", stringSeconds) + internalHumanSeconds(seconds) + "</span>";
        }
    }

    public String formatMinutes(long minutes) {
        if (minutes < TimeUtil.MINUTES_PER_DAY) {
            return String.format("%02d:%02d", minutes / 60, minutes % 60);
        } else {
            if (minutes <= TimeUtil.MINUTES_PER_WEEK * 2) {
                long days = minutes / TimeUtil.MINUTES_PER_DAY;
                minutes %= TimeUtil.MINUTES_PER_DAY;
                return String.format("%d:%02d:%02d", days, minutes / 60, minutes % 60);
            } else {
                return "";
            }
        }
    }

    public boolean toBoolean(Object object) {
        if (object == null) {
            return false;
        } else {
            if (object instanceof Boolean) {
                return (Boolean) object;
            } else {
                String lowercaseValue = object.toString().toLowerCase();
                return "true".equals(lowercaseValue) || "t".equals(lowercaseValue) || "yes".equals(lowercaseValue)
                        || "y".equals(lowercaseValue) || "1".equals(lowercaseValue) || "on".equals(lowercaseValue);
            }
        }
    }
}
