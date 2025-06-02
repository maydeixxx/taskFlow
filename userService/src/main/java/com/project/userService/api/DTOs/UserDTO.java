package com.project.userService.api.DTOs;

import lombok.Data;

@Data
public class UserDTO {
    private String userName;
    private String password;
    private String confirmPassword;
    private String email;
}
