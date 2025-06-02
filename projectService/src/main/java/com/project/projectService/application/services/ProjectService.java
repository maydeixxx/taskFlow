package com.project.projectService.application.services;

import com.project.projectService.application.interfaces.ProjectRepository;
import com.project.projectService.models.ProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public void saveProject(ProjectEntity project) {
        projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id).orElseThrow();
        projectRepository.delete(projectEntity);
    }

    public List<ProjectEntity> findAll() {
        return projectRepository.findAll();
    }

    public ProjectEntity findById(Long id) {
        return projectRepository.findById(id).orElseThrow();
    }

    public void updateProject(Long id, Map<String, Object> updates) {
        ProjectEntity project = projectRepository.findById(id).orElseThrow();
        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> project.setTitle(value.toString());
                case "body" -> project.setBody(value.toString());
                default -> throw new IllegalArgumentException("Unknown field to update");
            }
        });
    }
}
