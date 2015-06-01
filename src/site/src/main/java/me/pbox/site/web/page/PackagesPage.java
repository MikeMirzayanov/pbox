package me.pbox.site.web.page;

import com.google.inject.Inject;
import me.pbox.site.dao.PackageDao;
import me.pbox.site.index.IllegalQueryException;
import me.pbox.site.index.Index;
import me.pbox.site.model.Package;
import me.pbox.site.service.PackageService;
import me.pbox.site.util.JsonUtil;
import me.pbox.site.web.frame.PackageFrame;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.nocturne.annotation.Action;
import org.nocturne.annotation.Parameter;
import org.nocturne.link.Link;
import org.nocturne.main.ApplicationContext;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
@Link("packages;packages/{packageName};packages/{packageName}/{version};packages/find/tag/{tag}")
public class PackagesPage extends WebPage {
    @Parameter(stripMode = Parameter.StripMode.SAFE)
    private String tag;

    @Parameter(stripMode = Parameter.StripMode.SAFE)
    private String packageName;

    @Parameter(stripMode = Parameter.StripMode.SAFE)
    private String version;

    @Inject
    private PackageService packageService;

    @Inject
    private PackageDao packageDao;

    @Inject
    private Index index;

    @Override
    public void initializeAction() {
        super.initializeAction();
        unskipTemplate();
    }

    @Action("searchJson")
    public void onSearchJson(@Parameter(name = "query", stripMode = Parameter.StripMode.SAFE) String query,
                             @Parameter(name = "all", stripMode = Parameter.StripMode.SAFE) boolean all) {
        List<Map<String, Object>> jsonList;

        try {
            List<Package> packages = leaveLast(index.find(query), all);
            jsonList = new ArrayList<>();
            for (Package p : packages) {
                jsonList.add(p.toMap());
            }
        } catch (IllegalQueryException e) {
            jsonList = new ArrayList<>();
        }

        skipTemplate();
        getResponse().setContentType("application/json");
        Writer writer = getWriter();
        try {
            writer.write(JsonUtil.fromList(jsonList));
            writer.flush();
        } catch (IOException ignored) {
            // No operations.
        }
    }

    @Action("listJson")
    public void onListJson(@Parameter(name = "all", stripMode = Parameter.StripMode.SAFE) boolean all) {
        onSearchJson("", all);
    }

    @Action("infoJson")
    public void onInfoJson(
            @Parameter(name = "name", stripMode = Parameter.StripMode.SAFE) String name,
            @Parameter(name = "version", stripMode = Parameter.StripMode.SAFE) String version,
            @Parameter(name = "all", stripMode = Parameter.StripMode.SAFE) boolean all
    ) {
        List<Map<String, Object>> jsonList;

        try {
            List<Package> packages = leaveLast(index.findByName(name), all);
            jsonList = new ArrayList<>();
            for (Package p : packages) {
                jsonList.add(p.toMap());
            }
        } catch (IllegalQueryException e) {
            jsonList = new ArrayList<>();
        }

        if (StringUtils.isNotBlank(version)) {
            List<Map<String, Object>> previousJsonList = jsonList;
            jsonList = new ArrayList<>();
            for (Map<String, Object> map : previousJsonList) {
                if (version.equals(map.get("version").toString())) {
                    jsonList.add(map);
                }
            }
        }

        skipTemplate();
        getResponse().setContentType("application/json");
        Writer writer = getWriter();
        try {
            writer.write(JsonUtil.fromList(jsonList));
            writer.flush();
        } catch (IOException ignored) {
            // No operations.
        }
    }

    @Action("search")
    public void onSearch(@Parameter(name = "query", stripMode = Parameter.StripMode.SAFE) String query,
                         @Parameter(name = "all", stripMode = Parameter.StripMode.SAFE) boolean all) {
        try {
            setupPackages(leaveLast(index.find(query), all));
        } catch (IllegalQueryException e) {
            put("invalidQuery", true);
        }
    }

    private List<Package> leaveLast(List<Package> packages, boolean all) {
        if (all) {
            return packages;
        } else {
            LinkedHashMap<String, Package> lastPackages = new LinkedHashMap<>();
            for (Package p : packages) {
                if (!lastPackages.containsKey(p.getName())) {
                    lastPackages.put(p.getName(), p);
                } else {
                    Package before = lastPackages.get(p.getName());

                    if (Package.compareVersions(before.getVersion(), p.getVersion()) == -1) {
                        lastPackages.put(p.getName(), p);
                    } else {
                        if (before.getCreationTime().before(p.getCreationTime())) {
                            lastPackages.put(p.getName(), p);
                        }
                    }
                }
            }
            return new ArrayList<>(lastPackages.values());
        }
    }

    private void setupPackages(List<Package> packages) {
        List<String> packageItems = new ArrayList<>();

        for (Package p : packages) {
            PackageFrame packageFrame = ApplicationContext.getInstance().getInjector().getInstance(PackageFrame.class);
            packageFrame.setupPackage(p);
            packageItems.add(parse(packageFrame));
        }

        put("packageFrames", packageItems);
    }

    @Override
    public void action() {
        List<Package> packages = packageDao.findAll();

        if (containsFilter()) {
            packages = filter(packages);
        } else {
            packages = filterFirst(packages);
        }

        setupPackages(packages);
    }

    private List<Package> filterFirst(List<Package> packages) {
        List<Package> result = new ArrayList<>();
        Set<String> packageNames = new HashSet<>();
        for (Package p : packages) {
            if (packageNames.contains(p.getName())) {
                continue;
            }
            packageNames.add(p.getName());
            result.add(p);
        }
        return result;
    }

    private List<Package> filter(List<Package> packages) {
        List<Package> result = new ArrayList<>();
        for (Package p : packages) {
            if (StringUtils.isNotBlank(tag) && ArrayUtils.indexOf(p.getTags().split(","), tag) < 0) {
                continue;
            }
            if (StringUtils.isNotBlank(packageName) && !packageName.equalsIgnoreCase(p.getName())) {
                continue;
            }
            if (StringUtils.isNotBlank(version) && !version.equalsIgnoreCase(p.getVersion())) {
                continue;
            }
            result.add(p);
        }
        return result;
    }

    private boolean containsFilter() {
        return StringUtils.isNotBlank(tag) || StringUtils.isNotBlank(packageName);
    }

    @Override
    public String getTitle() {
        return $("Packages");
    }
}
