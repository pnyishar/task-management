package oasis.task.tech.repository.actors;

import oasis.task.tech.domains.actors.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:37PM
 * Project:task-management
 */

public interface TaskRepository extends JpaRepository<Task, String> {

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
            "ORDER BY " +
            "CASE WHEN t.priority = 'HIGH' THEN 1 " +
            "WHEN t.priority = 'NORMAL' THEN 2 " +
            "WHEN t.priority = 'LOW' THEN 3 ELSE 4 END, " +
            "t.dueDate ASC")
    Page<Task> getUserTaskList(@Param("userId") String userId, Pageable pageable);
}
