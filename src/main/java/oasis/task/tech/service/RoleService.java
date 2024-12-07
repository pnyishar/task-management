package oasis.task.tech.service;

import lombok.AllArgsConstructor;
import oasis.task.tech.constants.UserType;
import oasis.task.tech.domains.security.Permission;
import oasis.task.tech.domains.security.Role;
import oasis.task.tech.dto.security.PermissionDto;
import oasis.task.tech.repository.security.RoleRepository;
import oasis.task.tech.util.Utility;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
@Service
@AllArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    private final PermissionService permissionService;

    @CacheEvict("users-roles")
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(Long id) {
        return roleRepository.getOne(id);
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Cacheable("users-roles")
    public Set<Role> getRoles(UserType userType) {
        return new HashSet<>(Collections.singletonList(roleRepository.findByName(userType.name())));
    }

    public void updateSuperAdminPermissions(){
        Role role = findByName("SUPER_ADMIN");
        role.setPermissions(new HashSet<>(permissionService.findAll()));
        this.save(role);
    }


    public List<PermissionDto> getUserPermissions(String userType) {
        return getRoles(UserType.valueOf(userType)).stream().map(role
                        -> role.getPermissions().stream().map(perm -> Utility.map(perm, PermissionDto.class))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Set<Permission> getUsersPermissions(UserType userType){
        return getRoles(userType).stream().map(role
                        -> role.getPermissions().stream().collect(Collectors.toSet()))
                .flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @CacheEvict("users-roles")
    public void saveAll(List<Role> roles) {
        roles.forEach(this::save);
    }

    public Date getLastUpdate() {
        return roleRepository.getLastUpdate();
    }

    public List<Role> findByUpdatedAtAfter(Date lastUpdatedAfter) {
        return roleRepository.findByUpdatedAtAfter(lastUpdatedAfter);
    }
}
