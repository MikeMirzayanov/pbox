package me.pbox.site.model;

import org.jacuzzi.mapping.Id;

import java.io.Serializable;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public class ApplicationEntity implements Serializable {
    @Id
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPersistent() {
        return id != 0;
    }

    public String toString() {
        return String.format("%s {id=%d}", getClass().getSimpleName(), id);
    }

    @SuppressWarnings({"NonFinalFieldReferenceInEquals"})
    public boolean equals(Object o) {
        return this == o
                || o != null && getClass() == o.getClass() && id == ((ApplicationEntity) o).id;
    }

    @SuppressWarnings({"NonFinalFieldReferencedInHashCode"})
    public int hashCode() {
        return id == 0
                ? super.hashCode()
                : getClass().hashCode() * 31 + Long.valueOf(id).hashCode();
    }
}
