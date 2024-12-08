package oasis.task.tech.repository.actors;

import oasis.task.tech.domains.actors.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:37PM
 * Project:task-management
 */

public interface TaskRepository extends JpaRepository<Task, String> {
    @Query("SELECT t FROM Task t WHERE t.user.id=:userId")
    Page<Task> getUserTaskList(String userId, Pageable pageable);
}
