package com.project.projectService.application.api.controllers;

import com.project.projectService.application.api.DTOs.ProjectDTO;
import com.project.projectService.application.interfaces.ProjectMapper;
import com.project.projectService.application.services.ProjectService;
import com.project.projectService.models.ProjectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper mapper;

    @PostMapping("/save_project")
    public ResponseEntity<?> saveProject(@RequestBody ProjectDTO dto) {
        projectService.saveProject(mapper.dtoToEntity(dto));
        return ResponseEntity.ok().body("Project successfully saved");
    }

    @GetMapping("/all_projects")
    public ResponseEntity<?> findAllProject() {
        List<ProjectEntity> all = projectService.findAll();
        if (all.isEmpty()) {
            return ResponseEntity.badRequest().body("There are no any projects :(");
        }
        return ResponseEntity.ok(all.stream()
                .map(mapper::entityToDto)
                .toList()
        );
    }

    @GetMapping("/project_id/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        ProjectEntity byId = projectService.findById(id);
        if (byId == null) {
            return ResponseEntity.badRequest().body(String.format("there is no project with id = %s", id));
        }
        return ResponseEntity.ok(mapper.entityToDto(byId));
    }

    @PatchMapping("/update_project/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            projectService.updateProject(id, updates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        ProjectEntity byId = projectService.findById(id);
        return ResponseEntity.ok().body(String.format("Updated project = \n Title: %s\n Body: %s\n Members: %s",
                byId.getTitle(), byId.getBody(), byId.getMembers())
        );
    }

    @DeleteMapping("/delete_project/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }
}
