package com.project.userService.application.services;

import com.project.userService.api.DTOs.UserDTO;
import com.project.userService.application.interfaces.RoleRepository;
import com.project.userService.application.interfaces.UserRepository;
import com.project.userService.models.Role;
import com.project.userService.models.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    public void saveUser(UserDTO regUser) {
        User user = new User();
        user.setPassword(encoder.encode(regUser.getPassword()));
        user.setUsername(regUser.getUserName());
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
        return userRepository.findByUsername(username);
    }

    public void updateUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findUserById(id).orElseThrow();
        updates.forEach((key, value) -> {
            switch (key) {
                case "userName" -> user.setUsername(value.toString());
                case "password" -> user.setPassword(value.toString());
                case "email" -> user.setEmail(value.toString());
            //TODO    case "role" ->
            }
        });
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }
}
