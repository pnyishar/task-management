package oasis.task.tech.dto.security;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PermissionDto extends ModelDto {
    private String name;
}
