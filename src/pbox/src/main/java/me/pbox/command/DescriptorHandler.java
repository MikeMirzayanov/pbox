package me.pbox.command;

import me.pbox.env.Environment;
import me.pbox.option.Opts;
import me.pbox.xml.TemplateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.*;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class DescriptorHandler extends DefaultHandler {
    private static final Logger logger = Logger.getLogger(DescriptorHandler.class);
    private static final List<String> COMMANDS = Arrays.asList("install", "uninstall");

    private final Opts opts;
    private final List<String> tagStack = new ArrayList<>();
    private final List<Attributes> attributesStack = new ArrayList<>();
    private String content;
    private final File pboxDir;
    private final String processCommand;
    private final List<String> processedStatements = new ArrayList<>();
    private final Set<String> archs = new HashSet<>();

    public DescriptorHandler(File pboxDir, String processCommand, Opts opts) {
        this.pboxDir = pboxDir;
        this.processCommand = processCommand;
        this.opts = new Opts(opts);
    }

    public Opts getOpts() {
        return opts;
    }

    public List<String> getProcessedStatements() {
        return Collections.unmodifiableList(processedStatements);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tagStack.add(localName);
        attributesStack.add(attributes);
        content = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content += new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        int index = tagStack.size() - 1;
        String tag = tagStack.get(index);
        Attributes attributes = attributesStack.get(index);
        tagStack.remove(index);
        attributesStack.remove(index);

        if (tagStack.size() == 1 && !COMMANDS.contains(tag)) {
            content = TemplateUtil.process(content, opts);

            boolean ok = true;
            int attributesLength = attributes.getLength();
            for (int i = 0; i < attributesLength; i++) {
                String name = attributes.getLocalName(i);
                String value = attributes.getValue(i);

                String optionValue = opts.get(name) == null ? "" : opts.get(name);
                if (!optionValue.equals(value)) {
                    ok = false;
                }
            }

            if (ok) {
                content = TemplateUtil.process(content, opts);
                opts.put(tag, content);
                //logger.info("Run set operation '" + tag + "':='" + content + "'.");
            }

            if (tag.equals("archs")) {
                String[] items = content.split("[,;]+");
                for (String item : items) {
                    if (!StringUtils.isNoneBlank()) {
                        archs.add(item);
                    }
                }

                String arch = Environment.getArch();
                if (archs.contains(arch)) {
                    opts.put("arch", arch);
                } else {
                    // Prefer 32
                    if (archs.contains("32")) {
                        opts.put("arch", "32");
                    }
                    if (archs.contains("64")) {
                        opts.put("arch", "64");
                    }
                }

                String chosenArch = opts.get("arch");
                if (archs.contains(chosenArch)) {
                    opts.put("arch", chosenArch);
                    logger.info("Use version for architecture '" + chosenArch + "'.");
                } else {
                    throw new RuntimeException("Use architecture '" + chosenArch + "', but it is not supported. Supported are '" + content + "'.");
                }
            }
        }

        if (tagStack.size() == 2 && tagStack.get(1).equals("install") && processCommand.equals("install")) {
            handleInstall(tag);
        }

        if (tagStack.size() == 2 && tagStack.get(1).equals("uninstall") && processCommand.equals("uninstall")) {
            handleUninstall(tag);
        }
    }

    private void handleUninstall(String tag) {
        content = TemplateUtil.process(content, opts);
        processedStatements.add(content);

        String strHomedir = opts.get("homedir");
        if (StringUtils.isBlank(strHomedir)) {
            throw new RuntimeException("Can't find homedir.");
        }

        processCommands(tag, new File(strHomedir));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void handleInstall(String tag) {
        content = TemplateUtil.process(content, opts);
        processedStatements.add(content);

        String strHomedir = opts.get("homedir");
        if (StringUtils.isBlank(strHomedir)) {
            throw new RuntimeException("Can't find homedir.");
        }

        File homedir = new File(strHomedir);
        homedir.mkdirs();

        processCommands(tag, homedir);
    }

    private void processCommands(String tag, File homedir) {
        switch (tag) {
            case "copy":
                CommandUtil.copy(pboxDir, content, homedir);
                break;
            case "remove":
                CommandUtil.remove(pboxDir, content, homedir);
                break;

            case "path":
                CommandUtil.path(pboxDir, content, homedir, opts);
                break;
            case "unpath":
                CommandUtil.unpath(pboxDir, content, homedir);
                break;

            case "env":
                CommandUtil.env(pboxDir, content, homedir);
                break;
            case "unenv":
                CommandUtil.unenv(pboxDir, content, homedir);
                break;

            case "msi":
                CommandUtil.msi(pboxDir, content, homedir);
                break;
            case "unmsi":
                CommandUtil.unmsi(pboxDir, content, homedir);
                break;

            case "exec":
                CommandUtil.exec(pboxDir, content, homedir);
                break;

            case "script":
                CommandUtil.script(pboxDir, content, homedir);
                break;

            default:
                throw new RuntimeException("Unexpected element '" + tag + "' in install.");
        }
    }
}
