package com.project.notificationService.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.notificationService.models.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topicPartitions = @TopicPartition(topic = "notificationHandler", partitions = {"0, 1, 2, 3"}), groupId = "Notifications")
    private void handleNotifications(ConsumerRecord<String, String> record) {
        log.info("New message in topic notificationHandler. Partition: {}, Value: {}", record.partition(), record.value());
        try {
            switch (record.partition()) {
                case 0 -> {
                    Map<String, Object> userData = objectMapper.readValue(record.value(), new TypeReference<>() {});
                    log.info("Parsed data: {}", userData);
                    NotificationEvent event = new NotificationEvent();
                    event.setEventType("registration");
                    event.setUsername(userData.get("username").toString());
                    event.setEmail(userData.get("email").toString());
                    notificationService.sendMail(event);
                }

                case 1 -> {
                    Long projectId = Long.parseLong(record.key());
                    List<Map<String, Object>> users = objectMapper.readValue(record.value(), new TypeReference<>() {});
                    List<String> emails = users.stream()
                            .map(user -> user.get("email").toString())
                            .toList();
                    List<String> usernames = users.stream()
                            .map(user -> user.get("username").toString())
                            .toList();
                    for (Map<String, Object> user : users) {
                        NotificationEvent event = new NotificationEvent();
                        event.setEventType("newProject");
                        event.setUsername(getStringValue(user, "username"));
                        event.setEmail(getStringValue(user, "email"));
                        event.setProjectId(projectId);
                        notificationService.sendMail(event);
                    }
                }

                case 2 -> {
                    Map<String, Object> taskData = objectMapper.readValue(record.key(), new TypeReference<>() {});
                    Map<String, Object> userData = objectMapper.readValue(record.value(), new TypeReference<>() {});
                    String email = userData.get("email").toString();
                    String username = userData.get("username").toString();
                    String title = taskData.get("title").toString();
                    Long taskId = Long.parseLong(taskData.get("id").toString());
                    NotificationEvent event = new NotificationEvent();
                    if (username != null || email != null) {
                        event.setEventType("newTask");
                        event.setTaskId(taskId);
                        event.setTitle(title);
                        event.setEmail(email);
                        event.setUsername(username);
                        notificationService.sendMail(event);
                    } else {
                        throw new NullPointerException("Email or username is null.");
                    }
                }

            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message. Message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to handle notification. Message: {}", e.getMessage());
        }
    }

    public void setNullEvent(NotificationEvent event) {
        event.setProjectId(null);
        event.setTitle(null);
        event.setEmail(null);
        event.setEventType(null);
        event.setUsername(null);
        event.setTaskId(null);
        event.setDeadline(null);
    }

    private String getStringValue(Map<String, Object> data, String key) {
        String dataString = null;
        if (data.get(key) != null) {
            dataString = data.get(key).toString();
        }
        return dataString;
    }
}
