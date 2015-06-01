package me.pbox.command;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.pbox.Console;
import me.pbox.env.Environment;
import me.pbox.option.Opts;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class FindCommand implements Command {
    private static final String FIND_URL = Environment.getPboxSiteUrl() + "/packages?action=searchJson";

    static String doGet(String s) throws IOException {
        URL url = new URL(s);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        byte[] result = IOUtils.toByteArray(inputStream);
        inputStream.close();
        connection.disconnect();
        return new String(result, "UTF-8");
    }

    @Override
    public void run(Opts opts, String... args) {
        List<Map<String, Object>> packages = findPackages(args, opts.has("all"));

        printPackageCount(packages.size());
        int index = 0;
        for (Map<String, Object> p : packages) {
            index++;
            printPackage(index, p);
        }
    }

    static void printPackage(int index, Map<String, Object> p) {
        String title = StringUtils.trimToEmpty((String) p.get("title"));
        if (title.length() > 60) {
            title = title.substring(0, 60) + "...";
        }
        Console.println(StringUtils.rightPad(Integer.toString(index), 4)
                + StringUtils.rightPad(p.get("name").toString(), 30)
                + StringUtils.rightPad(p.get("version").toString(), 20)
                + StringEscapeUtils.escapeJava(title));
    }

    static void printPackageCount(int packageCount) {
        Console.println("Found " + packageCount + " package(s).");
    }

    static List<Map<String, Object>> findPackages(String[] args, boolean all) {
        StringBuilder query = new StringBuilder();
        for (String arg : args) {
            query.append(arg).append(' ');
        }

        try {
            String url = FIND_URL + "&query=" + URLEncoder.encode(query.toString().trim(), "UTF-8");
            if (all) {
                url += "&all=true";
            }
            String json = doGet(url);

            Type type = new TypeToken<List<Map<String, Object>>>() {
            }.getType();

            return new Gson().fromJson(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Unsupported UTF-8.", e);
        }
    }
}
