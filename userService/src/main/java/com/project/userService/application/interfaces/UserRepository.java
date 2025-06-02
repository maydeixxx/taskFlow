package com.project.userService.application.interfaces;

import com.project.userService.models.Role;
import com.project.userService.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);
    Optional<List<User>> findUsersByRoles(Role role);
    Optional<User> findByUsername(String username);
}
