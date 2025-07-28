package com.project.userService.api.controllers;

import com.project.userService.api.DTOs.UpdateUserDTO;
import com.project.userService.api.DTOs.UserDTO;
import com.project.userService.api.exceptions.PasswordIncorrectException;
import com.project.userService.api.exceptions.UserAlreadyExists;
import com.project.userService.application.services.UserService;
import com.project.userService.models.Role;
import com.project.userService.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/reg")
    public ResponseEntity<?> regUser(@RequestBody UserDTO regUser) {
        if (!regUser.getPassword().equals(regUser.getConfirmPassword())) {
            throw new PasswordIncorrectException("password is wrong");
        }
        if (userService.findByUsername(regUser.getUserName()) != null) {
            throw new UserAlreadyExists(String.format("user with name[%s] already exists", regUser.getUserName()));
        }
        userService.saveUser(regUser);
        return ResponseEntity.ok().body(String.format("%s, you successfully registered", regUser.getUserName()));
    }

    @PutMapping("/add_project/{userId}/{projectId}")
    public ResponseEntity<?> addProjectToUser(@PathVariable Long userId, @PathVariable Long projectId) {
        try {
            userService.addProjectToUser(userId, projectId, 1);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/remove_project/{userId}/{projectId}")
    public ResponseEntity<?> removeProject(@PathVariable Long projectId, @PathVariable Long userId) {
        try {
            userService.removeProjectFromUser(userId, projectId);
            return ResponseEntity.ok(String.format("Project %s removed from user %s", projectId, userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body(String.format("User by id = %s deleted", id));
    }

    @PutMapping("/update_user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO userDTO) {
        try {
            userService.updateUser(id, userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        User userById = userService.findUserById(id);
        return ResponseEntity.ok().body(String.format("User successfully updated!\n Name: %s\n Email: %s\n Roles: %s"
                , userById.getUsername(), userById.getEmail(), userById.getRoles()));
    }

    @GetMapping("/all_users")
    public ResponseEntity<?> allUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/user_id/{id}")
    public ResponseEntity<?> findUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users_roles/{role}")
    public ResponseEntity<?> findUserByRole(@PathVariable Role role) {
        try {
            return ResponseEntity.ok(userService.findUsersByRole(role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
