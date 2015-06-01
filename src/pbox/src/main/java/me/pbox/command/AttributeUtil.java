package me.pbox.command;

import org.xml.sax.Attributes;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class AttributeUtil {
    public static String getValue(Attributes attributes, String key) {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getLocalName(i).equals(key)) {
                return attributes.getValue(i);
            }
        }
        return null;
    }
}
