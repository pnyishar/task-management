package oasis.task.tech.repository.security;


import oasis.task.tech.domains.security.Role;
import oasis.task.tech.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by: Paul Nyishar
 * Date : 12/6/2024
 * Project : task-management
 */
public interface RoleRepository extends BaseRepository<Role, Long> {
    Role findByName(String name);

    @Query("SELECT MAX(a.updatedAt) FROM Role a")
    Date getLastUpdate();

    List<Role> findByUpdatedAtAfter(Date lastUpdatedAfter);
}
