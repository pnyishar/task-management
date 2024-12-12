package oasis.task.tech.service.interfaces;

import oasis.task.tech.constants.UserType;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.domains.security.Role;
import oasis.task.tech.dto.actors.*;
import org.springframework.data.domain.Page;

import javax.validation.constraints.NotNull;
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
    AdminDashboardDto getAdminDashboard();
    UserDashboardData getUserDashboard(String userId);
    User getCurrentUser();
    String updateUser(ProfileDto profileDto, String userId);
    long count();
    Optional<User> save(@NotNull(message = "Cannot save a null entity") User user);
    Page<User> getAllUsers(String searchTerm, int page, int limit);
}
