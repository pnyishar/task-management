
package oasis.task.tech.service;

import lombok.AllArgsConstructor;
import oasis.task.tech.constants.UserPermission;
import oasis.task.tech.domains.security.Permission;
import oasis.task.tech.repository.security.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */

@Service
@AllArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission save(Permission permission){
        return permissionRepository.save(permission);
    }

    public List<Permission> findAll(){
        return permissionRepository.findAll();
    }


    public  Permission findById(Long id){
        return  permissionRepository.getOne(id);
    }

    public  Permission findByName(String name){
        return  permissionRepository.findByName("ROLE_" + name);
    }

    public Permission find(UserPermission userPermission) {
        return permissionRepository.findByName("ROLE_" + userPermission.name());
    }

    public void saveAll(Collection<Permission> permissions) {
        permissions.forEach(this::save);
    }

    public Date getLastUpdate() {
        return permissionRepository.getLastUpdate();
    }

    public List<Permission> findByUpdatedAtAfter(Date lastUpdatedAfter) {
        return permissionRepository.findByUpdatedAtAfter(lastUpdatedAfter);
    }
}
