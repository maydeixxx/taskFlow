package com.project.projectService.application.services;

import com.project.projectService.application.interfaces.ProjectRepository;
import com.project.projectService.models.ProjectEntity;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectProducer producer;

    public void saveProject(ProjectEntity project) {
        project.setStatus("NOT_STARTED");
        projectRepository.save(project);
        producer.sendNewProject(project.getMembers(), project.getId());
        log.info("Отправлено сообщение в saveProjectToUser = {}, {}", project.getMembers(), project.getId());
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
                case "members" -> {
                    Collection<Long> members = project.getMembers();
                    Long member = Long.valueOf(value.toString());
                    if (members.contains(member)) {
                        members.remove(member);
                    } else {
                        members.add(member);
                    }
                    project.setMembers(members);
                }
                default -> throw new IllegalArgumentException("Unknown field to update");
            }
        });
        projectRepository.save(project);
    }

    public void addMember(Long userId, Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow();
        Collection<Long> members = project.getMembers();
        if (members.contains(userId)) {
            log.error("Пользователь уже есть в участниках проекта {}", projectId);
        }
        members.add(userId);
        project.setMembers(members);
        projectRepository.save(project);
    }

    public Collection<Long> getMembers(Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException(String.format("Project %s not found", projectId)));
        return project.getMembers();
    }
}
