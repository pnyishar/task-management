package oasis.task.tech.controller;

import oasis.task.tech.domains.actors.User;
import oasis.task.tech.dto.JsonResponse;
import oasis.task.tech.dto.actors.UserDto;
import oasis.task.tech.dto.actors.UserResponse;
import oasis.task.tech.mappers.UserMapper;
import oasis.task.tech.service.interfaces.UserService;
import oasis.task.tech.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Author: Paul Nyishar
 * Date:12/7/24
 * Time:3:14PM
 * Project:task-management
 */
@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "signUp")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto){
        String response = userService.signUpUser(userDto);

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true, response, "Registration Successful!!"), HttpStatus.CREATED);
    }

    @GetMapping("current")
    public ResponseEntity<?> getCurrentUser(){
        User user = userService.getCurrentUser();

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true,
                        Utility.map(user, UserResponse.class),
                        "Current user retrieved Successfully"),
                HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @GetMapping("all")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int limit,
                                         @RequestParam(required = false) String searchTerm) {

        Page<User> userPage = userService.getAllUsers(searchTerm,
                page, limit);

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true,
                        UserMapper.mapToUserResponse(userPage, page, limit),
                        "Users retrieved Successfully"),
                HttpStatus.OK);
    }
}
