package oasis.task.tech.service.impl;

import com.google.common.base.Strings;
import oasis.task.tech.constants.MessageConstant;
import oasis.task.tech.constants.Status;
import oasis.task.tech.constants.UserType;
import oasis.task.tech.domains.actors.Task;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.domains.security.Role;
import oasis.task.tech.dto.actors.AdminDashboardDto;
import oasis.task.tech.dto.actors.UserDashboardData;
import oasis.task.tech.dto.actors.UserDto;
import oasis.task.tech.exception.BadRequestException;
import oasis.task.tech.exception.NotFoundException;
import oasis.task.tech.repository.actors.TaskRepository;
import oasis.task.tech.repository.actors.UserRepository;
import oasis.task.tech.service.RoleService;
import oasis.task.tech.service.interfaces.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:2:50PM
 * Project:task-management
 */

@Service
public class UserServiceImpl extends BaseServiceImpl<User, String> implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleService roleService;
    private TaskRepository taskRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleService roleService, TaskRepository taskRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.taskRepository = taskRepository;
    }

    @Override
    @CacheEvict(value = {"users-email", "users-id"}, allEntries = true)
    public Optional<User> save(User user) {
        this.updateUserRoleIfEmptyAndUserTypeIsKnown(user);
        return super.save(user);
    }

    @Override
    public Page<User> getAllUsers(String searchTerm, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit,
                Sort.by("createdAt").descending());
        if (Strings.isNullOrEmpty(searchTerm)) {
            searchTerm = null;
        }

        return userRepository.findAll(pageable);
    }

    private void updateUserRoleIfEmptyAndUserTypeIsKnown(User user) {
        if ((user.getRoles() == null || user.getRoles().isEmpty()) && user.getUserType() != UserType.UNKNOWN){
            user.setRoles(roleService.getRoles(user.getUserType()));
        }
    }

    @Override
    public String signUpUser(UserDto userDto) {
        // Validate input fields
        validateUserInput(userDto);

        // Create and save the user using the builder pattern
        User user = User.builder()
                .fullName(userDto.getFullName())
                .phone(userDto.getPhone())
                .email(userDto.getEmail())
                .userType(UserType.USER)
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(getUserRoles(UserType.USER))
                .extraPermissions(roleService.getUsersPermissions(UserType.USER))
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    private void validateUserInput(UserDto userDto) {
        // Check for empty fields
        if (isNullOrEmpty(userDto.getFullName()) ||
                isNullOrEmpty(userDto.getPhone()) ||
                isNullOrEmpty(userDto.getEmail()) ||
                isNullOrEmpty(userDto.getPassword()) ||
                isNullOrEmpty(userDto.getRpassword())) {
            throw new BadRequestException(MessageConstant.EMPTY_DATA_FIELDS);
        }

        // Check if passwords match
        if (!userDto.getPassword().equals(userDto.getRpassword())) {
            throw new BadRequestException(MessageConstant.PASSWORD_MISMATCH);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new BadRequestException(MessageConstant.EMAIL_EXISTS);
        }

        // Check if phone number already exists
        if (userRepository.existsByPhone(userDto.getPhone())) {
            throw new BadRequestException(MessageConstant.PHONE_NUMBER_EXISTS);
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    @Cacheable("users-roles")
    public Set<Role> getUserRoles(UserType userType) {
        return roleService.getRoles(userType);
    }

    @Override
    public AdminDashboardDto getAdminDashboard() {
        AdminDashboardDto dashboardDto = new AdminDashboardDto();

        dashboardDto.setNumberOfUsers(userRepository.count());
        dashboardDto.setNumberOfTasks(taskRepository.count());
        dashboardDto.setNumberOfCompletedTasks(taskRepository.countByStatus(Status.COMPLETED));

        return dashboardDto;
    }

    @Override
    public UserDashboardData getUserDashboard(String userId) {
        UserDashboardData dashboardData = new UserDashboardData();

        long createdTasks = taskRepository.countByUserIdAndDeletedFalse(userId);
        long completedTasks = taskRepository.countByUserIdAndStatusAndDeletedFalse(userId, Status.COMPLETED);
        long waitingTasks = taskRepository.countByUserIdAndStatusAndDeletedFalse(userId, Status.WAITING);

        dashboardData.setNumberOfCreatedTasks(createdTasks);
        dashboardData.setNumberOfCompletedTasks(completedTasks);
        dashboardData.setNumberOfWaitingTasks(waitingTasks);

        return dashboardData;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    @Override
    public String updateUser(UserDto userDto, String userId) {
        // Fetch the existing user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Update the fields
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        if (userDto.getFullName() != null) user.setFullName(userDto.getFullName());
        if (userDto.getPhone() != null) user.setPhone(userDto.getPhone());

        // Save the updated user
        userRepository.save(user);

        return "User updated successfully";
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
