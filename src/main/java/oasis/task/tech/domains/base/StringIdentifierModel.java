package oasis.task.tech.domains.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;


/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

@Getter
@Setter
@MappedSuperclass
@ToString
@EqualsAndHashCode(callSuper = false)
public abstract class StringIdentifierModel extends Audit<String> {
    @Id
    @GenericGenerator(
            name = "custom-uuid",
            strategy = "oasis.task.tech.domains.base.CustomUuidGenerator"
    )
    @GeneratedValue(generator = "custom-uuid", strategy = GenerationType.AUTO)
    protected String id;
}
