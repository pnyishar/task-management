package oasis.task.tech.domains.base;

import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

public class Generator {

    @Nullable
    static Serializable getId(Object obj) {
        if (obj instanceof Identifiable) {
            Identifiable identifiable = (Identifiable) obj;
            Serializable id = identifiable.getId();
            if (id != null) return id;
        }
        return null;
    }
}
