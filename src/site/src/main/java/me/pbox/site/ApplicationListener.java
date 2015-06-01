package me.pbox.site;

import com.codeforces.commons.io.http.HttpMethod;
import com.codeforces.commons.io.http.HttpRequest;
import com.codeforces.commons.process.ThreadUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ApplicationListener implements ServletContextListener {
    private static final AtomicBoolean running = new AtomicBoolean();
    private static final AtomicReference<String> UPDATE_PACKAGES_URL = new AtomicReference<>();

    public static void setUpdatePackagesUrl(String updatePackagesUrl) {
        UPDATE_PACKAGES_URL.set(updatePackagesUrl);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        running.set(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running.get()) {
                    ThreadUtil.sleep(TimeUnit.SECONDS.toMillis(30));

                    final String updatePackagesUrl = UPDATE_PACKAGES_URL.get();
                    if (StringUtils.isNotBlank(updatePackagesUrl)) {
                        HttpRequest.create(updatePackagesUrl).setMethod(HttpMethod.POST).executeAndReturnResponse();
                    }
                }
            }
        }).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        running.set(false);
    }
}
