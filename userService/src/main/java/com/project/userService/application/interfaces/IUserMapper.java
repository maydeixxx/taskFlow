package com.project.userService.application.interfaces;

import com.project.userService.api.UserDTO;
import com.project.userService.models.User;
import com.project.userService.models.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {
    User entityToDomain(UserEntity user);
    UserEntity domainToEntity(User user);
    UserDTO domainToDto(User user);
    User dtoToDomain(UserDTO user);
}
