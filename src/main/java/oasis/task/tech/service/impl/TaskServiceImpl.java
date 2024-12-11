package oasis.task.tech.service.impl;

import com.google.common.base.Strings;
import oasis.task.tech.domains.actors.Task;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.dto.actors.RecentTaskDto;
import oasis.task.tech.dto.actors.TaskDto;
import oasis.task.tech.exception.NotFoundException;
import oasis.task.tech.repository.actors.TaskRepository;
import oasis.task.tech.repository.actors.UserRepository;
import oasis.task.tech.service.interfaces.TaskService;
import oasis.task.tech.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:43PM
 * Project:task-management
 */
@Service
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
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
                .status(taskDto.getStatus())
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

    @Override
    public Page<Task> getUserTasks(String searchTerm, int page, int limit, String userId) {
        Pageable pageable = PageRequest.of(page, limit);

        if (Strings.isNullOrEmpty(searchTerm)) {
            searchTerm = null;
        }

        // Fetch tasks with sorting applied in the query
        Page<Task> tasks = taskRepository.getUserTaskList(userId, pageable);

        return tasks;
    }

    @Override
    public String updateTask(TaskDto taskDto, String taskId) {
        // Fetch the existing task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));

        // Update the fields
        if (taskDto.getTitle() != null) task.setTitle(taskDto.getTitle());
        if (taskDto.getDescription() != null) task.setDescription(taskDto.getDescription());
        if (taskDto.getPriority() != null) task.setPriority(taskDto.getPriority());
        if (taskDto.getDueDate() != null) task.setDueDate(taskDto.getDueDate());

        // Save the updated task
        taskRepository.save(task);

        return "Task updated successfully";
    }

    @Override
    public String deleteTask(String taskId) {
        // Fetch the existing task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));

        // Mark the task as deleted instead of actually deleting it
        task.setDeleted(true);
        taskRepository.save(task);

        return "Task marked as deleted successfully";
    }

    @Override
    public List<RecentTaskDto> getUserRecentTasks(String userId) {
        List<Task> tasks = taskRepository.findTop5ByUserIdOrderByDueDateDesc(userId);

        // Convert Task entities to RecentTaskDto
        return tasks.stream()
                .map(task -> new RecentTaskDto(
                        task.getTitle(),
                        task.getStatus(),
                        task.getDueDate()
                ))
                .collect(Collectors.toList());
    }
}
