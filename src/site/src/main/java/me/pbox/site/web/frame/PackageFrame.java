package me.pbox.site.web.frame;

import com.codeforces.commons.text.StringUtil;
import com.codeforces.commons.xml.XmlUtil;
import me.pbox.site.exception.ApplicationException;
import me.pbox.site.model.Package;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class PackageFrame extends ApplicationFrame {
    private Package p;

    public void setupPackage(Package p) {
        this.p = p;
    }

    @Override
    public void action() {
        put("p", p);
        put("tags", p.getTags().split(","));
        put("groups", p.getGroups().split(","));
        put("archs", p.getArchs().split(","));
        put("versions", sort(p.getVersions().split(",")));

        try {
            put("homedir", XmlUtil.extractFromXml(new ByteArrayInputStream(p.getDescriptor().getBytes()), "/pbox/homedir", String.class));
        } catch (IOException e) {
            throw new ApplicationException("Can't get homedir from " + p + '.', e);
        }
    }

    private static String[] sort(String[] strings) {
        StringUtil.sortStringsSmart(strings);
        return strings;
    }
}
