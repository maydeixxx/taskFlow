package com.project.userService.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.userService.api.exceptions.KafkaException;
import com.project.userService.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, String> template;
    private final ObjectMapper objectMapper;

    public void sendUserToSubscribe(Long userId, Long projectId) {
        try {
            template.send("saveProjectToUser", 1, userId.toString(), projectId.toString());
        } catch (Exception e) {
            throw new KafkaException(e.getMessage());
        }
    }

    public void sendRegisteredUser(User user) {
        Map<String, Object> data = userToMap(user);
        try {
            String message = objectMapper.writeValueAsString(data);
            template.send("notificationHandler", 0, user.getId().toString(), message);
        } catch (JsonProcessingException e) {
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
