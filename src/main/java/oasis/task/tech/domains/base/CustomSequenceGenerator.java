package oasis.task.tech.domains.base;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serializable;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

public class CustomSequenceGenerator extends SequenceStyleGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Serializable id = Generator.getId(obj);
        if (id != null) return id;
        return (Serializable) super.generate(session, obj);
    }
}
