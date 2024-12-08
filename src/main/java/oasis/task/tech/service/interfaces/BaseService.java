package oasis.task.tech.service.interfaces;

import oasis.task.tech.domains.base.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:53PM
 * Project:task-management
 */

public interface BaseService<T extends Audit<ID>, ID extends Serializable> {
    boolean existsById(ID id);

    boolean deleteModelById(ID id);

    boolean deleteModel(T model);

    Optional<T> findOneById(ID id);

    T getOne(ID id);

    List<T> findAll();

    Page<T> findAll(Pageable pageable);

    Optional<T> update(@NotNull(message = "Cannot save a null entity") T model);

    Optional<T> save(@NotNull(message = "Cannot save a null entity") T model);

    T saveModel(@NotNull(message = "Cannot save a null entity") T model);

    List<T>  saveAll(List<T> models) ;

    default Date getLastUpdate() {
        return new Date(10000000L);
    }

    List<T> findAllDeleted(Class<T> clazz, String tableName);

    void deleteAll(List<T> models);
}
