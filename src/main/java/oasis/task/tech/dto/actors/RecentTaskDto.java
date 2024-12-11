package oasis.task.tech.dto.actors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import oasis.task.tech.constants.Status;

import java.time.LocalDate;

/**
 * Author: Paul Nyishar
 * Date:12/11/24
 * Time:2:09PM
 * Project:task-management
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentTaskDto {
    private String title;

    private Status status;

    private LocalDate dueDate;
}
