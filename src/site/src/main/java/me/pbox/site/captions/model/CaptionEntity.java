package me.pbox.site.captions.model;

import org.nocturne.main.ApplicationContext;

import java.util.*;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 *         Date: 06.05.11
 */
public final class CaptionEntity implements Comparable<CaptionEntity> {
    private static final ApplicationContext APPLICATION_CONTEXT = ApplicationContext.getInstance();

    private static final Map<String, String> EMPTY_ADDITIONAL_FIELDS =
            Collections.unmodifiableMap(new LinkedHashMap<String, String>());

    private static final Set<String> EMPTY_IMPORTANT_FIELD_NAMES =
            Collections.unmodifiableSet(new LinkedHashSet<String>());

    private final long id;
    private final String name;
    private final String caption;
    private final Map<String, String> additionalFields;
    private final Set<String> importantFieldNames;

    private volatile int hash;

    public CaptionEntity(long id, String name,
                         Map<String, String> additionalFields, Set<String> importantFieldNames) {
        this(id, name, APPLICATION_CONTEXT.$(name), additionalFields, importantFieldNames);
    }

    public CaptionEntity(long id, String name, String caption,
                         Map<String, String> additionalFields, Set<String> importantFieldNames) {
        this.id = id;
        this.name = name;
        this.caption = caption;

        this.additionalFields = additionalFields == null ?
                EMPTY_ADDITIONAL_FIELDS :
                Collections.unmodifiableMap(new LinkedHashMap<>(additionalFields));


        this.importantFieldNames = importantFieldNames == null ?
                EMPTY_IMPORTANT_FIELD_NAMES :
                Collections.unmodifiableSet(new LinkedHashSet<>(importantFieldNames));
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCaption() {
        return caption;
    }

    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
    public Set<String> getImportantFieldNames() {
        return importantFieldNames;
    }

    @Override
    public int compareTo(CaptionEntity o) {
        int captionComparison = caption.compareTo(o.caption);

        if (captionComparison != 0) {
            return captionComparison;
        }

        return ((Long) id).compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        return this == o
                || o != null && getClass() == o.getClass() && id == ((CaptionEntity) o).id;
    }

    @SuppressWarnings({"NonFinalFieldReferencedInHashCode"})
    @Override
    public int hashCode() {
        int h = hash;

        if (h == 0) {
            h = ((Long) id).hashCode();
            hash = h;
        }

        return h;
    }

    public static Comparator<CaptionEntity> getByNameComparator() {
        return new Comparator<CaptionEntity>() {
            @Override
            public int compare(CaptionEntity o1, CaptionEntity o2) {
                int nameComparison = o1.getName().compareTo(o2.getName());

                if (nameComparison != 0) {
                    return nameComparison;
                }

                return ((Long) o1.getId()).compareTo(o2.getId());
            }
        };
    }
}
