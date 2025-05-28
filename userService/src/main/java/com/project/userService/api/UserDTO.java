package com.project.userService.api;

import com.project.userService.models.valueObject.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String password;
    private String email;
    private Role role;
}
