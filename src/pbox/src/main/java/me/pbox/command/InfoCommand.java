package me.pbox.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.pbox.Console;
import me.pbox.env.Environment;
import me.pbox.option.Opts;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class InfoCommand implements Command {
    private static final String INFO_URL = Environment.getPboxSiteUrl() + "/packages?action=infoJson";

    @Override
    public void run(Opts opts, String... args) {
        List<Map<String, Object>> packages = new ArrayList<>();

        for (String name : args) {
            packages.addAll(findPackages(name, opts.get("version"), opts.has("all")));
        }

        FindCommand.printPackageCount(packages.size());
        int index = 0;
        for (Map<String, Object> p : packages) {
            index++;
            FindCommand.printPackage(index, p);
            if (p.get("description") != null) {
                String description = p.get("description").toString().trim();
                Console.println(StringEscapeUtils.escapeJava(description));
            }

            int width = 14;
            if (p.get("tags") != null) {
                Console.println(StringUtils.rightPad("Tags:", width) + StringEscapeUtils.escapeJava(p.get("tags").toString().trim()));
            }
            if (p.get("archs") != null) {
                Console.println(StringUtils.rightPad("Archs:", width) + StringEscapeUtils.escapeJava(p.get("archs").toString().trim()));
            }
            if (p.get("downloadCount") != null) {
                Console.println(StringUtils.rightPad("Downloads:", width) + Math.round(Double.parseDouble(p.get("downloadCount").toString().trim())));
            }
            if (p.get("versions") != null) {
                Console.println(StringUtils.rightPad("Versions:", width) + StringEscapeUtils.escapeJava(p.get("versions").toString().trim()));
            }
            if (p.get("authors") != null) {
                Console.println(StringUtils.rightPad("Authors:", width) + StringEscapeUtils.escapeJava(p.get("authors").toString().trim()));
            }
            Console.println("************************************************");
        }
    }

    private List<Map<String, Object>> findPackages(String name, String version, boolean all) {
        try {
            String url = INFO_URL + "&name=" + URLEncoder.encode(name.trim(), "UTF-8");
            if (!StringUtils.isNoneBlank(version)) {
                url += "&version=" + StringUtils.trimToEmpty(version);
            }
            if (all) {
                url += "&all=true";
            }
            String json = FindCommand.doGet(url);

            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();

            return new Gson().fromJson(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Unsupported UTF-8.", e);
        }

    }
}
