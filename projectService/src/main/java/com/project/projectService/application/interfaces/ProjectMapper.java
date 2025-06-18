package com.project.projectService.application.interfaces;

import com.project.projectService.api.DTOs.ProjectDTO;
import com.project.projectService.models.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(source = "tasks", target = "tasks")
    ProjectDTO entityToDto(ProjectEntity entity);

    @Mapping(source = "tasks", target = "tasks")
    ProjectEntity dtoToEntity(ProjectDTO dto);
}
