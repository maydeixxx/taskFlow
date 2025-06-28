package com.project.projectService.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.projectService.application.interfaces.ProjectRepository;
import com.project.projectService.models.ProjectEntity;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectProducer producer;
    private final ObjectMapper objectMapper;
    private final List<Map<String, Object>> cachedUsers = new ArrayList<>();

    public void saveProject(ProjectEntity project) {
        project.setStatus("NOT_STARTED");
        projectRepository.save(project);
        producer.sendNewProject(project.getMembers(), project.getId());
        log.info("Sent message to saveProjectToUser = {}, {}", project.getMembers(), project.getId());
        //notification
        try {
            Thread.sleep(1500);
            String message = objectMapper.writeValueAsString(cachedUsers);
            producer.sendNewProjectToNotification(project.getId(), message);
            log.info("USERS FOR PROJECT {}: [ {} ]", project.getId(), cachedUsers);
            cachedUsers.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "usernames", partitions = {"0"}), groupId = "usernames")
    public void getUsernamesResponse(ConsumerRecord<String, String> record) {
        try {
            List<Map<String, Object>> users = objectMapper.readValue(record.value(), new TypeReference<>() {});
            log.info("USERS FROM RECORD[ {} ]", users);
            cachedUsers.addAll(users);
            log.info("CACHED USERS: {}", cachedUsers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void addTaskToProject(Long taskId, Long projectId) {
        ProjectEntity project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException(String.format("Project %s not found", projectId)));
        Collection<Long> tasks = project.getTasks();
        if (tasks.contains(taskId)) {
            throw new IllegalArgumentException(String.format("Project %s already contains task %s", projectId, taskId));
        }
        tasks.add(taskId);
        project.setTasks(tasks);
    }
}
