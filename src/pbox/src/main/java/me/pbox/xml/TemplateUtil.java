package me.pbox.xml;

import me.pbox.env.Environment;
import me.pbox.option.Opts;
import org.apache.commons.lang3.StringUtils;

import java.util.NoSuchElementException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class TemplateUtil {
    public static String process(String format, Opts opts) {
        if (StringUtils.isBlank(format)) {
            return format;
        }

        while (true) {
            boolean updated = false;

            main:
            for (int i = 0; i < format.length(); i++) {
                if (format.charAt(i) == '}') {
                    for (int j = i - 2; j >= 0; j--) {
                        if (format.charAt(j) == '$' && format.charAt(j + 1) == '{') {
                            String name = format.substring(j + 2, i);

                            if (!opts.has(name)) {
                                throw new NoSuchElementException("Can't find variable '" + name + "'.");
                            }

                            format = format.substring(0, j) + opts.get(name) + format.substring(i + 1);
                            updated = true;
                            break main;
                        }
                    }
                }
                if (format.charAt(i) == '%') {
                    for (int j = i - 1; j >= 0; j--) {
                        if (format.charAt(j) == '%') {
                            String name = format.substring(j + 1, i);
                            String value = null;

                            for (String item : name.split(":")) {
                                String itemValue = getEnvOrFallback(item, opts);
                                if (StringUtils.isNoneEmpty(itemValue)) {
                                    value = itemValue;
                                    break;
                                }
                            }

                            if (StringUtils.isBlank(value)) {
                                throw new NoSuchElementException("Can't find environment variable '" + name + "'.");
                            }

                            format = format.substring(0, j) + value + format.substring(i + 1);
                            updated = true;

                            break main;
                        }
                    }
                }
            }

            if (!updated) {
                break;
            }
        }

        return format;
    }

    private static String getEnvOrFallback(String item, Opts opts) {
        if (StringUtils.isBlank(item) || !item.trim().equals(item)) {
            throw new IllegalArgumentException("Expected variable expression, but '" + item + "' found.");
        }

        if (item.startsWith("\"") && item.endsWith("\"")) {
            return item.substring(1, item.length() - 1);
        }

        if (item.startsWith("${") && item.endsWith("}")) {
            String key = item.substring(2, item.length() - 1);
            return opts.get(key);
        }

        String value = Environment.get(item);
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        return value;
    }
}
