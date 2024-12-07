package oasis.task.tech.repository;

import oasis.task.tech.domains.base.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:1:23PM
 * Project:task-management
 */
@NoRepositoryBean
public interface BaseRepository<T extends Audit<ID>, ID extends Serializable> extends JpaRepository<T, ID> {
    <S extends T> S cleanSave(S entity);

    void validate(ID id);

    default void validate(T entity) {
        if (entity != null) this.validate(entity.getId());
    }

    default void validate(Collection<T> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(this::validate);
        }
    }
}
