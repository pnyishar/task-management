package oasis.task.tech.dto.actors;

import lombok.Data;
import oasis.task.tech.constants.UserType;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:25PM
 * Project:task-management
 */
@Data
public class UserResponse {
    private Long id;

    private String fullName;

    private String phone;

    private String email;

    private UserType userType;
}
