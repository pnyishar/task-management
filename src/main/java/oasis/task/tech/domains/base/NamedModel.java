package oasis.task.tech.domains.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

@Getter
@Setter
@MappedSuperclass
@ToString(callSuper = true)
public abstract class NamedModel extends Model {

    @Column(unique = true, nullable = false)
    protected String name;
}
