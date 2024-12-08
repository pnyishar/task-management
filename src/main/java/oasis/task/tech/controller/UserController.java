package oasis.task.tech.controller;

import oasis.task.tech.domains.actors.User;
import oasis.task.tech.dto.JsonResponse;
import oasis.task.tech.dto.actors.UserDto;
import oasis.task.tech.dto.actors.UserResponse;
import oasis.task.tech.service.interfaces.UserService;
import oasis.task.tech.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("current-user")
    public ResponseEntity<?> getCurrentUser(){
        User user = userService.getCurrentUser();

        return new ResponseEntity<>(
                new JsonResponse(HttpStatus.OK, true,
                        Utility.map(user, UserResponse.class),
                        "Current user retrieved Successfully"),
                HttpStatus.OK);

    }
}
