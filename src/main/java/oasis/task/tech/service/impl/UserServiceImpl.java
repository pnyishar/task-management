package oasis.task.tech.service.impl;

import com.google.common.base.Strings;
import oasis.task.tech.constants.MessageConstant;
import oasis.task.tech.constants.UserType;
import oasis.task.tech.domains.actors.User;
import oasis.task.tech.domains.security.Role;
import oasis.task.tech.dto.actors.UserDto;
import oasis.task.tech.exception.BadRequestException;
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

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
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
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }

    @Override
    public long count() {
        return userRepository.count();
    }
}
