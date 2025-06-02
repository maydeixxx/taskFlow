package com.project.userService.api.DTOs;

import lombok.Data;

@Data
public class AuthRequest {
    private String password;
    private String username;
    private String email;
}
