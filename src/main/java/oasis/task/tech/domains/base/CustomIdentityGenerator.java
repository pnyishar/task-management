package oasis.task.tech.domains.base;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class CustomIdentityGenerator implements IdentifierGenerator {

    private AtomicLong sequence = new AtomicLong(1);

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        Serializable id = Generator.getId(obj);
        if (id != null) return id;
        return sequence.getAndIncrement();
    }
}
