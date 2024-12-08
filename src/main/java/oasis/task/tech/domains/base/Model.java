package oasis.task.tech.domains.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public abstract class Model extends Audit<Long> {

    @Id
    @GenericGenerator(
            name = "custom_identity",
            strategy = "oasis.task.tech.domains.base.CustomIdentityGenerator"
    )
    @GeneratedValue(generator = "custom_identity", strategy = GenerationType.IDENTITY)
    protected Long id;

    public Model() {}

    public Model(Long id) {this.id = id;}
}
