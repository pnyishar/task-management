package oasis.task.tech.dto.security;

import lombok.Data;

import java.util.Set;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Data
public class RoleDto {
    private long id;

    private String name;

    private Set<PermissionDto> permissions;
}
