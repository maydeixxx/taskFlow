package com.project.userService.application.interfaces;

import com.project.userService.models.User;
import com.project.userService.models.valueObject.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IUserService {
    void saveUser(User user);
    void deleteUser(Long id);
    List<User> findAllUsers();
    User findUserById(Long id);
    List<User> findUsersByRole(Role role);
    void updateUser(Long id, Map<String, Object> updates);
}
