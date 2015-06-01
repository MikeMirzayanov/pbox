package me.pbox.command;

import me.pbox.compress.CompressUtil;
import me.pbox.env.Environment;
import me.pbox.pkg.Pkg;
import me.pbox.pkg.PkgUtil;
import me.pbox.registry.RegistryUtil;
import me.pbox.xml.TemplateUtil;
import me.pbox.xml.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class InstallCommand implements Command {
    private static final Logger logger = Logger.getLogger(InstallCommand.class);

    @Override
    public void run(String... args) {
        if (args.length > 1) {
            for (String arg : args) {
                run(arg);
            }
        }

        run(new Pkg(args[0]));
    }

    private void run(Pkg pkg) {
        logger.info("Processing the package " + pkg + ".");
        pkg.setLatestVersion();

        if (pkg.getVersion() == null) {
            throw new RuntimeException("Can't find any version of `" + pkg + "`. Are you sure the package exists?");
        }

        logger.info("Installing " + pkg + "...");

        File pboxFile = PkgUtil.findPbox7zFile(pkg);
        if (pboxFile == null) {
            throw new RuntimeException("Can't find " + pkg + ".pbox.7z for `" + pkg + "`. Are you sure the package exists?");
        }

        final File pboxDir = new File(Environment.getPboxTempAsFile(), pkg.toString());
        CompressUtil.uncompress(pboxFile, pboxDir);
        final Map<String, String> options = new HashMap<>();

        DefaultHandler handler = new DefaultHandler() {
            private List<String> tags = new ArrayList<>();
            private String content;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                tags.add(localName);
                content = "";
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                content += new String(ch, start, length);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                String tag = tags.get(tags.size() - 1);
                tags.remove(tags.size() - 1);

                if (tags.size() == 1 && (!tag.equals("install") && !tag.equals("uninstall"))) {
                    content = TemplateUtil.process(content, options);
                    options.put(tag, content);
                }

                if (tags.size() == 2 && tags.get(1).equals("install")) {
                    content = TemplateUtil.process(content, options);

                    if (!options.containsKey("homedir") || StringUtils.isBlank(options.get("homedir"))) {
                        throw new RuntimeException("Can't find homedir.");
                    }

                    File homedir = new File(options.get("homedir"));
                    //noinspection ResultOfMethodCallIgnored
                    homedir.mkdirs();

                    switch (tag) {
                        case "copy":
                            CommandUtil.copy(pboxDir, content, homedir);
                            break;
                        case "path":
                            CommandUtil.path(pboxDir, content, homedir);
                            break;
                        case "env":
                            CommandUtil.env(pboxDir, content, homedir);
                            break;
                        case "msi":
                            CommandUtil.msi(pboxDir, content, homedir);
                            break;
                        case "script":
                            CommandUtil.script(pboxDir, content, homedir);
                            break;
                        default:
                            throw new RuntimeException("Unexpected element '" + tag + "' in install.");
                    }
                }
            }
        };

        XmlUtil.traverse(Environment.getPboxXml(pboxDir), handler);

        RegistryUtil.write(pkg, pboxDir);
        File signalFile = new File(options.get("homedir"), "." + pkg.getName() + ".pbox");
        try {
            FileUtils.write(signalFile, pkg.toString());
        } catch (IOException e) {
            throw new RuntimeException("Can't write signal file " + signalFile + ".", e);
        }

        logger.info("Finished to install " + pkg + ".");
    }
}
