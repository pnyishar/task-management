package oasis.task.tech.controller;

import oasis.task.tech.domains.actors.Task;
import oasis.task.tech.dto.JsonResponse;
import oasis.task.tech.dto.actors.TaskDto;
import oasis.task.tech.mappers.TaskMapper;
import oasis.task.tech.service.interfaces.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:3:00PM
 * Project:task-management
 */

@RestController
@RequestMapping("api/v1/task/")
public class TaskController {
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("new")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskDto taskDto) {
        String newTaskResponse = taskService.createTask(taskDto);

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true, newTaskResponse), HttpStatus.CREATED
        );
    }

    @GetMapping("all")
    public ResponseEntity<?> getAllTasks(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int limit,
                                         @RequestParam(required = false) String searchTerm) {
        Page<Task> taskPage = taskService.getAllTask(searchTerm, page, limit);

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true,
                        TaskMapper.mapToTaskResponsePaginated(taskPage, page, limit),
                        "All Tasks Retrieved Successfully"),
                HttpStatus.OK);
    }

    @GetMapping("user/all")
    public ResponseEntity<?> getUserTasks(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int limit,
                                          @RequestParam(required = false) String searchTerm,
                                          @RequestParam("userId") String userId) {
        Page<Task> taskPage = taskService.getUserTasks(searchTerm, page, limit, userId);

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true,
                        TaskMapper.mapToTaskResponsePaginated(taskPage, page, limit),
                        "All User Tasks Retrieved Successfully"),
                HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<?> updateTask(@Valid @RequestBody TaskDto taskDto, @RequestParam String taskId) {
        String updateResponse = taskService.updateTask(taskDto, taskId);

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true, updateResponse), HttpStatus.OK
        );
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> deleteTask(@RequestParam String taskId) {
        String deleteResponse = taskService.deleteTask(taskId);

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true, deleteResponse), HttpStatus.OK
        );
    }
}
