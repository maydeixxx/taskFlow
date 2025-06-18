package com.project.analyticsService.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.analyticsService.application.interfaces.TaskAnalyticsRepository;
import com.project.analyticsService.models.TaskAnalytics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskAnalyticsService {
    private final TaskAnalyticsRepository taskAnalyticsRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "taskCompleted", groupId = "completeTasks")
    public void handleCompletedTasks(ConsumerRecord<String, String> record) {
        TaskAnalytics taskAnalytics = new TaskAnalytics();
        try {
            Map<String, Object> taskData = objectMapper.readValue(record.value(), new TypeReference<Map<String, Object>>() {});
            Long taskId = Long.parseLong(taskData.get("id").toString());
            Long projectId = Long.parseLong(taskData.get("projectId").toString());
            Long userId = Long.parseLong(taskData.get("memberId").toString());
            LocalDate completionTime= LocalDate.parse(taskData.get("completionTime").toString());
            String status = taskData.get("status").toString();

            taskAnalytics.setCompletionTime(completionTime);
            taskAnalytics.setProjectId(projectId);
            taskAnalytics.setUserId(userId);
            taskAnalytics.setStatus(status);
            taskAnalytics.setTaskId(taskId);
            taskAnalyticsRepository.save(taskAnalytics);
            log.info("Saved completed analytics task");
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
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
}
