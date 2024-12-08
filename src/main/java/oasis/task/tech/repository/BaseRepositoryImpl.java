package oasis.task.tech.repository;

import oasis.task.tech.domains.base.Audit;
import oasis.task.tech.exception.ValidationConstraintException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:26PM
 * Project:task-management
 */
public class BaseRepositoryImpl<T extends Audit<ID>, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
    private final JpaEntityInformation entityInformation;

    BaseRepositoryImpl(JpaEntityInformation entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
    }


    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public <S extends T> S cleanSave(S entity) {
        return super.saveAndFlush(entity);
    }

    @Override
    public void validate(ID id) {
        if (id != null && !this.existsById(id)) {
            throw new ValidationConstraintException("The entity with ID :" + id + "does not exist");
        }
    }
}
