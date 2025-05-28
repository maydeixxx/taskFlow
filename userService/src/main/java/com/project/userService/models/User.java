package com.project.userService.models;

import com.project.userService.models.valueObject.Role;
import lombok.Data;

@Data
public class User {
    private Long id;
    private String userName;
    private String password;
    private String email;
    private Role role;
}
