package com.project.projectService.application.api.DTOs;

import lombok.Data;

import java.util.Collection;

@Data
public class ProjectDTO {
    private Long id;
    private String title;
    private String body;
    private String status;
    private Collection<Long> members;
}
