package oasis.task.tech.dto.actors;

import lombok.Data;
import oasis.task.tech.constants.Priority;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

/**
 * Author: Paul Nyishar
 * Date:12/8/24
 * Time:2:40PM
 * Project:task-management
 */
@Data
public class TaskDto {
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private LocalDate dueDate;
}
