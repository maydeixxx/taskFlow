package com.project.userService.application.interfaces;

import com.project.userService.models.UserEntity;
import com.project.userService.models.valueObject.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findUserEntityById(Long id);

    List<UserEntity> findUserEntitiesByRole(Role role);
}
