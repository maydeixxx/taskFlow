package com.project.userService.models;

import com.project.userService.models.valueObject.Role;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String userName;
    private String password;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
}
