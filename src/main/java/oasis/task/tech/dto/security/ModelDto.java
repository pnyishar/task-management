package oasis.task.tech.dto.security;


import lombok.Getter;
import lombok.Setter;
import oasis.task.tech.domains.base.Identifiable;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Getter
@Setter
public class ModelDto implements Identifiable<Long> {
    protected Long id;
}
