package oasis.task.tech.dto.actors;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:25PM
 * Project:task-management
 */
@Data
public class UserDto {
    @NotNull
    private String fullName;

    @NotNull
    private String phone;

    @NotNull
    private String password;

    @NotNull
    private String rpassword;

    @NotNull
    private String email;
}
