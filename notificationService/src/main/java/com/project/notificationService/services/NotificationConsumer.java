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

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topicPartitions = @TopicPartition(topic = "notificationHandler", partitions = {"0, 1, 2, 3"}), groupId = "Notifications")
    private void handleNotifications(ConsumerRecord<String, String> record) {
        NotificationEvent event = new NotificationEvent();
        log.info("New message in topic notificationHandler. Partition: {}, Value: {}", record.partition(), record.value());
        try {
            Map<String, Object> userData = objectMapper.readValue(record.value(), new TypeReference<Map<String, Object>>() {});
            log.info("Parsed data: {}", userData);
            event.setEventType("registration");
            event.setUsername(userData.get("username").toString());
            event.setEmail(userData.get("email").toString());
            notificationService.sendMail(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message. Message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to handle notification. Message: {}", e.getMessage());
        }
    }
}
