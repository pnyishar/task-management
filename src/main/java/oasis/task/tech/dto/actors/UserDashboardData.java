package oasis.task.tech.dto.actors;

import lombok.Data;

/**
 * Author: Paul Nyishar
 * Date:12/10/24
 * Time:9:22PM
 * Project:task-management
 */
@Data
public class UserDashboardData {
    private long numberOfCreatedTasks;

    private long numberOfCompletedTasks;

    private long numberOfWaitingTasks;
}
