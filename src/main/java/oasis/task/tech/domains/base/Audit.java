package oasis.task.tech.domains.base;


import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Getter
@Setter
@MappedSuperclass
@ToString(callSuper = true)
public abstract class Audit<ID extends Serializable> implements Identifiable<ID> {

    @Temporal(TemporalType.DATE)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    public Audit() {
    }

    public void onPersist() {
        this.prePersist();
    }

    public void delete(){
        deletedAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audit<?> audit = (Audit<?>) o;
        return Objects.equals(this.getId(), audit.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public void onUpdate() {
        this.updatedAt = new Date();
        this.preUpdate();
    }

}
