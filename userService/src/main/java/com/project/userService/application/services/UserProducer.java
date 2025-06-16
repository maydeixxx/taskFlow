package com.project.userService.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, String> template;

    public void sendUserToSubscribe(Long userId, Long projectId) {
        template.send("saveProjectToUser", 1, userId.toString(), projectId.toString());
    }
}
