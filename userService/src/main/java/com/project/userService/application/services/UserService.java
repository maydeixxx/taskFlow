package com.project.userService.application.services;

import com.project.userService.application.interfaces.IUserMapper;
import com.project.userService.application.interfaces.IUserRepository;
import com.project.userService.application.interfaces.IUserService;
import com.project.userService.models.User;
import com.project.userService.models.UserEntity;
import com.project.userService.models.valueObject.Role;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository repository;
    private final IUserMapper mapper;

    @Override
    public void saveUser(User user) {
        repository.save(mapper.domainToEntity(user));
    }

    @Override
    public void deleteUser(Long id) {
        repository.delete(repository.findUserEntityById(id));
    }

    @Override
    public List<User> findAllUsers() {
        return repository.findAll().stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    public User findUserById(Long id) {
        return mapper.entityToDomain(repository.findUserEntityById(id));
    }

    @Override
    public List<User> findUsersByRole(Role role) {
        return repository.findUserEntitiesByRole(role).stream()
                .map(mapper::entityToDomain)
                .toList();
    }

    @Override
    public void updateUser(Long id, Map<String, Object> updates) {
        UserEntity userEntity = repository.findUserEntityById(id);
        User user = mapper.entityToDomain(userEntity);
        updates.forEach((key, value) -> {
            switch (key) {
                case "userName" -> user.setUserName(value.toString());
                case "password" -> user.setPassword(value.toString());
                case "email" -> user.setEmail(value.toString());
                case "role" -> user.setRole(Role.valueOf(value.toString()));
            }
        });
        repository.save(mapper.domainToEntity(user));
    }
}
