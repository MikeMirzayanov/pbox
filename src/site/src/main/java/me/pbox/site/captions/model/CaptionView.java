package me.pbox.site.captions.model;

import org.nocturne.main.ApplicationContext;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 *         Date: 29.04.11
 */
public final class CaptionView implements Comparable<CaptionView> {
    private static final ApplicationContext APPLICATION_CONTEXT = ApplicationContext.getInstance();

    private final String name;
    private final String caption;

    private volatile int hash;

    public CaptionView(String name) {
        this(name, APPLICATION_CONTEXT.$(name));
    }

    public CaptionView(String name, String caption) {
        this.name = name;
        this.caption = caption;
    }

    public String getName() {
        return name;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public int compareTo(CaptionView o) {
        return caption.compareTo(o.caption);
    }

    @Override
    public boolean equals(Object o) {
        return this == o
                || o != null && getClass() == o.getClass() && name.equals(((CaptionView) o).name);
    }

    @SuppressWarnings({"NonFinalFieldReferencedInHashCode"})
    @Override
    public int hashCode() {
        int h = hash;

        if (h == 0) {
            h = name.hashCode();
            hash = h;
        }

        return h;
    }
}
