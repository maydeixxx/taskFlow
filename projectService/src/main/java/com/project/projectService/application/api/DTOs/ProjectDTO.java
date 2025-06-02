package com.project.projectService.application.api.DTOs;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Data
public class ProjectDTO {
    private Long id;
    private String title;
    private String body;
    private Collection<Long> members;
}
