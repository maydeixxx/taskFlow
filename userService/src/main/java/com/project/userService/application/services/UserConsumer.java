package com.project.userService.application.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userService.api.exceptions.KafkaException;
import com.project.userService.models.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> template;

    @Transactional
    @KafkaListener(topicPartitions = @TopicPartition(topic = "saveProjectToUser", partitions = {"0"}), groupId = "subsToProjects")
    public void saveProjectToUser(ConsumerRecord<String, String> record) {
        try {
            log.info("Partition = {}", record.partition());
            Collection<Long> userIds = objectMapper.readValue(record.key(), new TypeReference<>() {
            });
            Long projectId = Long.parseLong(record.value());
            log.info("Received message in saveProjectToUser = {}, {}", userIds, projectId);
            userIds.forEach(id -> userService.addProjectToUser(id, projectId, 0));
            //send usernames back to topic
            List<Map<String, Object>> users = new ArrayList<>();
            for (Long userId : userIds) {
                users.add(userToMap(userService.findUserById(userId)));
            }
            String message = objectMapper.writeValueAsString(users);
            template.send("projectUsernames", projectId.toString(), message);
        } catch (Exception e) {
            throw new KafkaException(e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topicPartitions = @TopicPartition(topic = "saveTaskToProject", partitions = {"1"}), groupId = "subsToTask")
    public void handleTaskSub(ConsumerRecord<String, String> record) {
        log.info("MESSAGE = {}", record.value());
        Long taskId = Long.parseLong(record.key());
        Long userId = Long.parseLong(record.value());
        userService.addTaskToUser(userId, taskId);
        log.info("USERS UPDATED");
        //send usernames to notification topic
        try {
            Map<String, Object> userData = userToMap(userService.findUserById(userId));
            String userDataString = objectMapper.writeValueAsString(userData);
            template.send("taskUsernames", taskId.toString(), userDataString);
        } catch (Exception e) {
            throw new KafkaException(e.getMessage());
        }
    }

    public Map<String, Object> userToMap(User user) {
        return
                Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                );
    }
}
