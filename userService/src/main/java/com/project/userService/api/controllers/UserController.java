package com.project.userService.api.controllers;

import com.project.userService.api.DTOs.AppError;
import com.project.userService.api.DTOs.UserDTO;
import com.project.userService.application.services.UserService;
import com.project.userService.models.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/admin")
    public ResponseEntity<?> admin() {
        return ResponseEntity.ok().body("ADMIN");
    }

    @GetMapping("/secured")
    public ResponseEntity<?> securedZone() {
        return ResponseEntity.ok().body("You are in the secured zone");
    }

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
}
