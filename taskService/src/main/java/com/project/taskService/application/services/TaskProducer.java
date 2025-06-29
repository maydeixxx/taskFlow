package com.project.taskService.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskService.models.TaskEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskProducer {
    private final KafkaTemplate<String, String> template;
    private final ObjectMapper objectMapper;

    public void saveTaskToProject(Long projectId, Long taskId, Long memberId) {
        template.send("saveTaskToProject", 0, taskId.toString(), projectId.toString());
        template.send("saveTaskToProject", 1, taskId.toString(), memberId.toString());
    }

    public void sendTaskToAnalytics(Long taskId, TaskEntity taskEntity) {
        Map<String, Object> task = formatTaskToMap(taskEntity);
        try {
            String message = objectMapper.writeValueAsString(task);
            template.send("taskAnalytics", taskId.toString(), message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTaskToNotification(Map<String, Object> taskData, Map<String, Object> userData) {
        try {
            String userDataString = objectMapper.writeValueAsString(userData);
            String taskDataString = objectMapper.writeValueAsString(taskData);
            template.send("notificationHandler", 2, taskDataString, userDataString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Sent message to notificationHandler");
    }

    public void sendCompletedTaskToAnalytics(Long taskId, TaskEntity task) {
        Map<String, Object> taskToMap = formatTaskToMap(task);
        try {
            String message = objectMapper.writeValueAsString(taskToMap);
            template.send("completedTasks", taskId.toString(), message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    public Map<String, Object> formatTaskToMap(TaskEntity taskDTO) {
        return Map.of(
                "id", taskDTO.getId(),
                "title", taskDTO.getTitle(),
                "body", taskDTO.getBody(),
                "status", taskDTO.getStatus(),
                "completionTime", LocalDate.now(),
                "memberId", taskDTO.getMemberId(),
                "projectId", taskDTO.getProjectId()
        );
    }
}
