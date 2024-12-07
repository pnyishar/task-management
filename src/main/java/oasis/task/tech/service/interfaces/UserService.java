package oasis.task.tech.service.interfaces;

import jakarta.validation.constraints.NotNull;
import oasis.task.tech.constants.UserType;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.domains.security.Role;
import oasis.task.tech.dto.actors.UserDto;

import java.util.Optional;
import java.util.Set;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:25PM
 * Project:task-management
 */
public interface UserService {
    String signUpUser(UserDto userDto);
    Set<Role> getUserRoles(UserType userType);
    User getCurrentUser();
    long count();
    Optional<User> save(@NotNull(message = "Cannot save a null entity") User user);
}
