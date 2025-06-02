package com.project.userService.api.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class UpdateUserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
    private List<String> roles;
}
