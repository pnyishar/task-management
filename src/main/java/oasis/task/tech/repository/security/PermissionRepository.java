package oasis.task.tech.repository.security;

import oasis.task.tech.domains.security.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByName(String name);

    @Query("SELECT MAX(a.updatedAt) FROM Permission a")
    Date getLastUpdate();

    List<Permission> findByUpdatedAtAfter(Date lastUpdatedAfter);
}
