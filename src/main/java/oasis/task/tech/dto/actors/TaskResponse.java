package oasis.task.tech.dto.actors;

import lombok.Data;
import oasis.task.tech.constants.Priority;
import oasis.task.tech.constants.Status;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:4:13PM
 * Project:task-management
 */

@Data
public class TaskResponse {
    private String id;

    private String title;

    private String description;

    private Priority priority;

    private Status status;

    private LocalDate dueDate;
}
