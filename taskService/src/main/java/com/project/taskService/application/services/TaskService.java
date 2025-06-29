package com.project.taskService.application.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.taskService.api.DTOs.TaskUpdateDTO;
import com.project.taskService.application.interfaces.TaskRepository;
import com.project.taskService.models.TaskEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskProducer producer;
    private final ConcurrentHashMap<String, Object> cachedUser = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    public void saveTask(TaskEntity task) {
        taskRepository.save(task);
        Long taskId = task.getId();
        Long projectId = task.getProjectId();
        Long memberId = task.getMemberId();
        String title = task.getTitle();
        producer.saveTaskToProject(projectId, taskId, memberId);
        //notification
        try {
            Thread.sleep(1000);
            log.info("RECEIVED USER: {}", cachedUser);
            Map<String, Object> taskData = Map.of(
                    "id", taskId,
                    "title", title
            );
            producer.sendTaskToNotification(taskData, cachedUser);
            cachedUser.clear();
            log.info("Cleared cached user: {}", cachedUser);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        producer.sendTaskToAnalytics(taskId, task);
    }

    @KafkaListener(topicPartitions = @TopicPartition(topic = "taskUsernames", partitions = {"0"}), groupId = "usernames")
    public void handleUsernameFromUser(ConsumerRecord<String, String> record) {
        Long taskId = Long.parseLong(record.key());
        try {
            Map<String, Object> userData = objectMapper.readValue(record.value(), new TypeReference<>() {});
            cachedUser.put(
                    "username", userData.get("username").toString()
            );
            cachedUser.put(
                    "email", userData.get("email").toString()
            );
            log.info("CACHED USER: {}", cachedUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("task %s not found", id)));
        taskRepository.delete(task);
    }

    public void updateTask(Long id, TaskUpdateDTO taskUpdate) {
        TaskEntity task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("task %s not found", id)));
        switch (taskUpdate.getField()) {
            case "title" -> task.setTitle(taskUpdate.getTitle());
            case "body" -> task.setBody(taskUpdate.getBody());
            case "status" -> {
                task.setStatus(taskUpdate.getStatus());
                if (taskUpdate.getStatus().equals("DONE")) {
                    producer.sendCompletedTaskToAnalytics(task.getId(), task);
                }
            }
            case "deadline" -> task.setDeadline(taskUpdate.getDeadline());
            case "members" -> {
                task.setMemberId(taskUpdate.getMemberId());
            }
        }
        taskRepository.save(task);
    }

    public List<TaskEntity> findTasksByProject(Long projectId) {
        return taskRepository.findAll()
                .stream()
                .filter(task -> task.getProjectId().equals(projectId))
                .toList();
    }

    public TaskEntity findTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format("Task %s not found", id)));
    }
}
