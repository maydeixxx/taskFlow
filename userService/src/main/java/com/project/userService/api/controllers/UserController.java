package com.project.userService.api.controllers;

import com.project.userService.api.UserDTO;
import com.project.userService.application.interfaces.IUserMapper;
import com.project.userService.application.services.UserService;
import com.project.userService.models.User;
import com.project.userService.models.valueObject.Role;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class UserController {
    private final UserService service;
    private final IUserMapper mapper;

    @PostMapping("/save_user")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) {
        try {
            if (user.getUserName() == null || user.getPassword() == null || user.getEmail() == null) {
                return ResponseEntity.badRequest().body("fill all data");
            }
            service.saveUser(mapper.dtoToDomain(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("user saved");
    }

    @GetMapping("/user_id/{id}")
    public ResponseEntity<?> findUserById(@PathVariable Long id) {
        try {
            User userById = service.findUserById(id);
            UserDTO userDTO = mapper.domainToDto(userById);
            if (userDTO == null) {
                return ResponseEntity.badRequest().body("User not found");
            }
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all_users")
    public ResponseEntity<?> findAllUsers() {
        try {
            List<UserDTO> users = service.findAllUsers().stream()
                    .map(mapper::domainToDto)
                    .toList();
            if (users.isEmpty()) {
                return ResponseEntity.badRequest().body("There are no users");
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user_role/{role}")
    public ResponseEntity<?> findUsersByRole(@PathVariable Role role) {
        try {
            List<UserDTO> users = service.findUsersByRole(role).stream()
                    .map(mapper::domainToDto)
                    .toList();
            if (users.isEmpty()) {
                return ResponseEntity.badRequest().body("There are no users");
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/update_user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            service.updateUser(id, updates);
            return ResponseEntity.ok().body("user updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete_user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            service.deleteUser(id);
            return ResponseEntity.ok().body("user deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
