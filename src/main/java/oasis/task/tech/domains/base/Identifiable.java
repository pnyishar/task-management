package oasis.task.tech.domains.base;

import java.io.Serializable;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

public interface Identifiable<T extends Serializable> extends Serializable {

    T getId();

    void setId(T id);

    default void prePersist() {
    }

    default void preUpdate() {
    }
}
