package com.project.projectService.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectConsumer {
    private final ProjectService projectService;

    @Transactional
    @KafkaListener(topics = "saveProjectToUser", groupId = "subsToProjects")
    public void handleSubscribeRequest(ConsumerRecord<String, String> record) {
        if (record.partition() == 1) {
            Long userId = Long.parseLong(record.key());
            Long projectId = Long.parseLong(record.value());
            projectService.addMember(userId, projectId);
            log.info("Пользователь {} добавлен в проект {}", userId, projectId);
        }
    }

    @Transactional
    @KafkaListener(topics = "saveTaskToProject", groupId = "subsToTask")
    public void handleTaskRequest(ConsumerRecord<String, String> record) {
        log.info("NEW MESSAGE = {}", record.value());
        if (record.partition() == 0) {
            Long projectId = Long.parseLong(record.value());
            Long taskId = Long.parseLong(record.key());
            projectService.addTaskToProject(taskId, projectId);
            log.info("PROJECT UPDATED");
        }
    }
}
