package com.project.userService.application.services;

import com.project.userService.api.DTOs.UpdateUserDTO;
import com.project.userService.api.DTOs.UserDTO;
import com.project.userService.application.interfaces.RoleRepository;
import com.project.userService.application.interfaces.UserRepository;
import com.project.userService.models.Role;
import com.project.userService.models.User;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserProducer producer;

    public void saveUser(UserDTO regUser) {
        User user = new User();
        user.setPassword(encoder.encode(regUser.getPassword()));
        user.setUserName(regUser.getUserName());
        user.setEmail(regUser.getEmail());
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.delete(userRepository.findUserById(id).orElseThrow());
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public Optional<List<User>> findUsersByRole(Role role) {
        return userRepository.findUsersByRoles(role);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    @Transactional
    public void updateUser(Long id, UpdateUserDTO userDTO) {
        User user = userRepository.findUserById(id).orElseThrow();
        if (userDTO.getPassword() != null) {
            user.setPassword(userDTO.getPassword());
        }
        if (userDTO.getUsername() != null) {
            user.setUserName(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getRoles() != null) {
            Collection<Role> roles = new ArrayList<>();
            for (String role : userDTO.getRoles()) {
                roles.add(roleRepository.findByName(role)
                        .orElseThrow(() -> new IllegalArgumentException(String.format("Role with name %s not found", role))));
                user.getRoles().clear();
                user.setRoles(roles);
            }
        }
        if (userDTO.getProjects() != null) {
            user.setProjects(userDTO.getProjects());
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public void addProjectToUser(Long userId, Long projectId, int partition) {
        User user = userRepository.findUserById(userId).orElseThrow();
        Collection<Long> projects = user.getProjects();
        if (projects.contains(projectId)) {
            throw new IllegalArgumentException("Project already contains in your list");
        }
        if (partition == 1) {
           producer.sendUserToSubscribe(userId, projectId);
        }
        projects.add(projectId);
        user.setProjects(projects);
        userRepository.save(user);
    }

    public void removeProjectFromUser(Long userId, Long projectId) {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Collection<Long> projects = user.getProjects();
        if (!projects.contains(projectId)) {
            throw new IllegalArgumentException("Project already removed");
        }
        projects.remove(projectId);
        user.setProjects(projects);
        userRepository.save(user);
    }

    public void addTaskToUser(Long userId, Long taskId) {
        User user = userRepository.findUserById(userId).orElseThrow(() -> new NotFoundException(String.format("User %s not found", userId)));
        Collection<Long> tasks = user.getTasks();
        if (tasks.contains(taskId)) {
            throw new IllegalArgumentException("Task already contains");
        }
        tasks.add(taskId);
        user.setTasks(tasks);
        userRepository.save(user);
    }
}