package oasis.task.tech.repository.actors;

import oasis.task.tech.domains.actors.Task;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:37PM
 * Project:task-management
 */

public interface TaskRepository extends JpaRepository<Task, String> {
}
