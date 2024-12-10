package oasis.task.tech.mappers;

import oasis.task.tech.domains.actors.Task;
import oasis.task.tech.dto.PaginatedListDto;
import oasis.task.tech.dto.actors.TaskResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:4:12PM
 * Project:task-management
 */

public class TaskMapper {
    public static TaskResponse mapToTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setPriority(task.getPriority());
        taskResponse.setStatus(task.getStatus());
        taskResponse.setCreatedBy(task.getUser().getFullName());
        taskResponse.setDueDate(task.getDueDate());

        return taskResponse;
    }

    public static List<TaskResponse> mapToResponseList(
            List<Task> allTasks) {
        return allTasks.stream().map(TaskMapper::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    public static PaginatedListDto<TaskResponse> mapToTaskResponsePaginated(
            Page<Task> taskPage, int page, int limit) {
        PaginatedListDto<TaskResponse> paginatedListDto = new PaginatedListDto<>();
        paginatedListDto.setPage(page);
        paginatedListDto.setLimit(limit);
        paginatedListDto.setTotal(taskPage.getTotalElements());
        paginatedListDto.setEntities(mapToResponseList(taskPage.getContent()));
        return paginatedListDto;
    }
}
