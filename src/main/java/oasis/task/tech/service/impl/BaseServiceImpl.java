package oasis.task.tech.service.impl;

import oasis.task.tech.dao.Accessor;
import oasis.task.tech.domains.base.Audit;
import oasis.task.tech.exception.ObjectNotFoundException;
import oasis.task.tech.repository.BaseRepository;
import oasis.task.tech.service.interfaces.BaseService;
import oasis.task.tech.util.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:55PM
 * Project:task-management
 */

public class BaseServiceImpl<T extends Audit<ID>, ID extends Serializable> implements BaseService<T, ID> {
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private BaseRepository<T, ID> repository;

    public BaseRepository<T, ID> getRepository() {
        return repository;
    }

    public void setRepository(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    public BaseServiceImpl(BaseRepository<T, ID> repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Optional<T> findOneById(ID id) {
        return Objects.isNull(id) ? Optional.empty() : repository.findById(id);
    }

    @Override
    public T getOne(ID id) {
        if (!this.existsById(id)) throw  new ObjectNotFoundException("The Entity can not be found");
        return repository.getOne(id);
    }

    @Override
    public boolean existsById(ID id){
        return repository.existsById(id);
    }

    @Override
    public boolean deleteModelById(ID id) {
        return Objects.nonNull(id) && this.existsById(id) && delete(repository.getOne(id));
    }

    @Override
    public boolean deleteModel(T model) {
        return Objects.nonNull(model) && this.existsById(model.getId()) && this.delete(model);
    }

    public boolean delete(T model) {
        model.setDeletedAt(TimeProvider.now());
        repository.save(model);
        return true;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<T> update(@NotNull(message = "Cannot update a null entity") T model) {
        return Objects.isNull(model) || objectDoesNotExist(model)
                ? Optional.empty() : Optional.of(this.updateModel(model));
    }

    private boolean objectDoesNotExist(T model) {
        return Objects.isNull(model.getId()) || !repository.existsById(model.getId());
    }

    private T updateModel(T model) {
        T object = repository.getOne(model.getId());
        model.setCreatedAt(object.getCreatedAt());
        return repository.save(model);
    }

    @Override
    public Optional<T> save(@NotNull(message = "Cannot save a null entity") T model) {
        return Objects.isNull(model) ? Optional.empty() : Optional.of(repository.save(model));
    }

    @Override
    public T saveModel(@NotNull(message = "Cannot save a null entity") T model) {
        return repository.save(model);
    }

    @Override
    public List<T> saveAll(List<T> models) {
        return repository.saveAll(models);
    }

    @Override
    public List<T> findAllDeleted(Class<T> clazz, String tableName) {
        return Accessor.findAllBy(clazz, tableName, "deleted_at is not null");
    }

    @Override
    public void deleteAll(List<T> models) {
        for (T model : models) deleteModelById(model.getId());
    }
}
