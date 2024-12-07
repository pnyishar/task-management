package oasis.task.tech.mappers;

import oasis.task.tech.domains.security.Role;
import oasis.task.tech.dto.security.RoleDto;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:38PM
 * Project:task-management
 */

public class UserRoleMapper {
    public static RoleDto mapToDto(Role role) {
        if (role == null)
            return null;

        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setPermissions(PermissionMapper.mapToDtoList(role.getPermissions()));

        return dto;
    }

    public static Set<RoleDto> mapToDtoList(Set<Role> roles) {
        Set<RoleDto> roleDtos = new HashSet<>();
        roles.forEach(role -> roleDtos.add(mapToDto(role)));
        return roleDtos;
    }
}
