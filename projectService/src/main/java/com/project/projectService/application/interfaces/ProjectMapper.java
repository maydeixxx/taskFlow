package com.project.projectService.application.interfaces;

import com.project.projectService.application.api.DTOs.ProjectDTO;
import com.project.projectService.models.ProjectEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDTO entityToDto(ProjectEntity entity);
    ProjectEntity dtoToEntity(ProjectDTO dto);
}
