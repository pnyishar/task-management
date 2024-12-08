package oasis.task.tech.service.impl;

import com.google.common.base.Strings;
import oasis.task.tech.domains.actors.Task;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.dto.actors.TaskDto;
import oasis.task.tech.exception.NotFoundException;
import oasis.task.tech.repository.actors.TaskRepository;
import oasis.task.tech.repository.actors.UserRepository;
import oasis.task.tech.service.interfaces.TaskService;
import oasis.task.tech.service.interfaces.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:43PM
 * Project:task-management
 */
@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;
    private UserService userService;
    private UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserService userService, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public String createTask(TaskDto taskDto) {
        // Fetch the current user
        User user = userService.getCurrentUser();

        if (user == null) {
            throw new NotFoundException("Authenticated user not found");
        }

        // Build the task
        Task task = Task.builder()
                .title(taskDto.getTitle())
                .description(taskDto.getDescription())
                .priority(taskDto.getPriority())
                .dueDate(taskDto.getDueDate())
                .user(user)
                .build();

        // save the task
        taskRepository.save(task);

        return "Task created successfully";
    }

    @Override
    public Page<Task> getAllTask(String searchTerm, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit,
                Sort.by("createdAt").descending());
        if (Strings.isNullOrEmpty(searchTerm)) {
            searchTerm = null;
        }

        return taskRepository.findAll(pageable);
    }
}
