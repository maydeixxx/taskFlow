package com.project.userService.api.controllers;

import com.project.userService.api.DTOs.AppError;
import com.project.userService.api.DTOs.UpdateUserDTO;
import com.project.userService.api.DTOs.UserDTO;
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
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "пароли не совпадают", new Date()), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(regUser.getUserName()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "пользователь с таким именем уже сущетсвует", new Date()), HttpStatus.UNAUTHORIZED);
        }
        userService.saveUser(regUser);
        return ResponseEntity.ok().body(String.format("'%s', вы успешно зарегистрировались!", regUser.getUserName()));
    }

    @PatchMapping("/add_project/{userId}/{projectId}")
    public ResponseEntity<?> addProjectToUser(@PathVariable Long userId, @PathVariable Long projectId) {
        try {
            userService.addProjectToUser(userId, projectId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body(String.format("User by id = %s deleted", id));
    }

    @PatchMapping("/update_user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserDTO userDTO) {
        try {
            userService.updateUser(id, userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        User userById = userService.findUserById(id).orElseThrow();
        return ResponseEntity.ok().body(String.format("User successfully updated!\n Name: %s\n Email: %s\n Roles: %s"
                , userById.getUsername(), userById.getEmail(), userById.getRoles()));
    }

    @GetMapping("/all_users")
    public ResponseEntity<?> allUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/user_id/{id}")
    public ResponseEntity<?> findUserById(@PathVariable Long id) {
        User user = userService.findUserById(id).orElseThrow(() -> new IllegalArgumentException(String.format("User with id = %s not found", id)));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users_roles/{role}")
    public ResponseEntity<?> findUserByRole(@PathVariable Role role) {
        try {
            return ResponseEntity.ok(userService.findUsersByRole(role).orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
