package com.project.taskService.application.interfaces;

import com.project.taskService.api.DTOs.TaskDTO;
import com.project.taskService.models.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDTO entityToDto(TaskEntity taskEntity);
    TaskEntity dtoToEntity(TaskDTO taskDTO);
}
