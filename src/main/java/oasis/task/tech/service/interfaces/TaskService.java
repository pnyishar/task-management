package oasis.task.tech.service.interfaces;

import oasis.task.tech.domains.actors.Task;
import oasis.task.tech.dto.actors.TaskDto;
import org.springframework.data.domain.Page;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:40PM
 * Project:task-management
 */

public interface TaskService {
    String createTask(TaskDto taskDto);
    Page<Task> getAllTask(String searchTerm, int page, int limit);
    Page<Task> getUserTasks(String searchTerm, int page, int limit, String userId);
}
