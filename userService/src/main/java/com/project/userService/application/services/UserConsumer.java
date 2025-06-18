package com.project.userService.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "saveProjectToUser", groupId = "subsToProjects")
    public void saveProjectToUser(ConsumerRecord<String, String> record) {
        try {
            log.info("Partition = {}", record.partition());
            if (record.partition() == 0) {
                Collection<Long> userIds = objectMapper.readValue(record.key(), new TypeReference<Collection<Long>>() {
                });
                Long projectId = Long.parseLong(record.value());
                log.info("Получено сообщение в saveProjectToUser = {}, {}", userIds, projectId);
                userIds.forEach(id -> userService.addProjectToUser(id, projectId, 0));
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = "saveTaskToProject", groupId = "subsToTask")
    public void handleTaskSub(ConsumerRecord<String, String> record) {
        log.info("MESSAGE = {}", record.value());
        if (record.partition() == 1) {
            Long taskId = Long.parseLong(record.key());
            try {
                Set<Long> members = objectMapper.readValue(record.value(), new TypeReference<Set<Long>>() {});
                members.forEach(id -> userService.addTaskToUser(id, taskId));
                log.info("USERS UPDATED");
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }
    }
}
