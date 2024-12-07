package oasis.task.tech.mappers;

import oasis.task.tech.domains.security.Permission;
import oasis.task.tech.dto.security.PermissionDto;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:40 PM
 * Project:task-management
 */

public class PermissionMapper {
    public static Set<PermissionDto> mapToDtoList(Set<Permission> permissions) {
        return permissions.stream().map(PermissionMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    public static PermissionDto mapToDto(Permission permission) {
        PermissionDto permissionDto = new PermissionDto();
        permissionDto.setId(permission.getId());
        permissionDto.setName(permission.getName());
        return permissionDto;
    }
}
