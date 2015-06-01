package me.pbox.xml;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class TemplateUtil {
    public static String process(String format, Map<String, String> variables) {
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
                            if (!variables.containsKey(name)) {
                                throw new NoSuchElementException("Can't find variable '" + name + "'.");
                            }

                            format = format.substring(0, j) + variables.get(name) + format.substring(i + 1);
                            updated = true;
                            break main;
                        }
                    }
                }
                if (format.charAt(i) == '%') {
                    for (int j = i - 1; j >= 0; j--) {
                        if (format.charAt(j) == '%') {
                            String name = format.substring(j + 1, i);
                            String value = System.getenv(name);
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
}
