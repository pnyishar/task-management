package oasis.task.tech.domains.base;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import java.io.Serializable;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

public class CustomUuidGenerator extends UUIDGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object obj) {
        Serializable id = Generator.getId(obj);
        if (id != null) return id;
        return (Serializable) super.generate(session, obj);
    }
}
