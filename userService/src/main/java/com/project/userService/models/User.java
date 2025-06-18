package com.project.userService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @ElementCollection
    @CollectionTable(name = "user_projects",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "project_id")
    private Collection<Long> projects;

    @CollectionTable(name = "user_tasks",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "task_id")
    private Long taskId;
}
