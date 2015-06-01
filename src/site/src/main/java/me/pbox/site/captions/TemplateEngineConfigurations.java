/*
 * Copyright by Mike Mirzayanov
 */
package me.pbox.site.captions;

import freemarker.template.Configuration;
import org.nocturne.main.ReloadingContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class TemplateEngineConfigurations {
    private static final Set<Configuration> configurations = new HashSet<>();

    public static void add(Configuration configuration) {
        if (ReloadingContext.getInstance().isDebug()) {
            configurations.add(configuration);
        }
    }

    public static Set<Configuration> get() {
        return new HashSet<>(configurations);
    }
}
