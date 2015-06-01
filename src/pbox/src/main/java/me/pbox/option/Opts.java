package me.pbox.option;

import java.util.*;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class Opts {
    private Map<String, String> map = new HashMap<>();
    private Opts parentOpts;

    public Opts(Opts parentOpts) {
        this.parentOpts = parentOpts;
    }

    public Opts() {
        this.parentOpts = null;
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public boolean has(String... names) {
        if (parentOpts != null && parentOpts.has(names)) {
            return true;
        }

        for (String name : names) {
            if (map.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public String get(String... names) {
        if (parentOpts != null && parentOpts.has(names)) {
            return parentOpts.get(names);
        }

        for (String name : names) {
            if (map.containsKey(name)) {
                return map.get(name);
            }
        }
        return null;
    }

    public boolean has(Option option) {
        if (parentOpts != null && parentOpts.has(option)) {
            return true;
        }

        for (String name : option.getNames()) {
            if (map.containsKey(name)) {
                return true;
            }
        }

        return false;
    }

    public String get(Option option) {
        if (parentOpts != null && parentOpts.has(option)) {
            return parentOpts.get(option);
        }

        for (String name : option.getNames()) {
            if (map.containsKey(name)) {
                return map.get(name);
            }
        }

        return parentOpts == null ? null : parentOpts.get(option);
    }

    public Set<String> getKeys() {
        Set<String> keys = new TreeSet<>(map.keySet());
        if (parentOpts != null) {
            keys.addAll(parentOpts.getKeys());
        }
        return Collections.unmodifiableSet(keys);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("opts:\n");
        for (String key : getKeys()) {
            result.append(key).append('=').append(get(key)).append('\n');
        }
        return result.toString();
    }
}
