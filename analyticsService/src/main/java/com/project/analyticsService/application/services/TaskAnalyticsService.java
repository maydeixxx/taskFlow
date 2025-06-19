package com.project.analyticsService.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analyticsService.application.interfaces.TaskAnalyticsRepository;
import com.project.analyticsService.models.TaskAnalytics;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAnalyticsService {
    private final TaskAnalyticsRepository taskAnalyticsRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "taskAnalytics", groupId = "savedTasks")
    public void handleSavedTasks(ConsumerRecord<String, String> record) {
        try {
            Map<String, Object> taskData = objectMapper.readValue(record.value(), new TypeReference<Map<String, Object>>() {
            });
            TaskAnalytics taskAnalytics = formatRecordToAnalytics(taskData);
            taskAnalyticsRepository.save(taskAnalytics);
            log.info("Saved task\n{}", taskAnalytics);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "completedTasks", groupId = "completedTasks")
    public void handleCompletedTasks(ConsumerRecord<String, String> record) {
        try {
            Map<String, Object> taskData = objectMapper.readValue(record.value(), new TypeReference<Map<String, Object>>() {});
            Long taskId = Long.parseLong(taskData.get("id").toString());
            TaskAnalytics taskAnalytics = taskAnalyticsRepository.findTaskAnalyticsByTaskId(taskId).orElseThrow(() -> new NotFoundException(String.format("TaskAnalytics %s not found", taskId)));
            taskAnalytics.setStatus("DONE");
            taskAnalyticsRepository.save(taskAnalytics);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> calculateUserStatistic(Long userId) {
        double productivity = 0;
        List<TaskAnalytics> tasks = taskAnalyticsRepository.findByUserId(userId);
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
                .filter(task -> task.getStatus().equals("DONE"))
                .count();
        if (totalTasks > 0) {
            productivity = completedTasks * 100 / totalTasks;
        }
        return Map.of(
                "userId", userId,
                "productivity", productivity,
                "totalTasks", totalTasks,
                "completedTasks", completedTasks
        );
    }

    public Map<String, Object> calculateProjectStatistic(Long projectId) {
        double productivity = 0;
        List<TaskAnalytics> byProjectId = taskAnalyticsRepository.findByProjectId(projectId);

        long totalTasks = byProjectId.size();
        long completedTasks = byProjectId.stream()
                .filter(task -> task.getStatus().equals("DONE"))
                .count();
        if (totalTasks > 0) {
            productivity = (double) (completedTasks * 100 / totalTasks);
        }
        return Map.of(
                "projectId", projectId,
                "productivity", productivity,
                "totalTasks", totalTasks,
                "completedTasks", completedTasks
        );
    }

    public TaskAnalytics formatRecordToAnalytics(Map<String, Object> taskData) {
        TaskAnalytics taskAnalytics = new TaskAnalytics();
        try {
            Long taskId = Long.parseLong(taskData.get("id").toString());
            Long projectId = Long.parseLong(taskData.get("projectId").toString());
            Long userId = Long.parseLong(taskData.get("memberId").toString());
            LocalDate completionTime = LocalDate.parse(taskData.get("completionTime").toString());
            String status = taskData.get("status").toString();

            taskAnalytics.setCompletionTime(completionTime);
            taskAnalytics.setProjectId(projectId);
            taskAnalytics.setUserId(userId);
            taskAnalytics.setStatus(status);
            taskAnalytics.setTaskId(taskId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return taskAnalytics;
    }
}
