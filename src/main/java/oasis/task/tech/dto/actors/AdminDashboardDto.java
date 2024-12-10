package oasis.task.tech.dto.actors;

import lombok.Data;

/**
 * Author: Paul Nyishar
 * Date:12/10/24
 * Time:3:35AM
 * Project:task-management
 */
@Data
public class AdminDashboardDto {
    private long numberOfUsers;
    private long numberOfTasks;
    private long numberOfCompletedTasks;
}
