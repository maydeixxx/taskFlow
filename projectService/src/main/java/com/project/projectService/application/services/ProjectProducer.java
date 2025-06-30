package com.project.projectService.application.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ProjectProducer {
    private final KafkaTemplate<String, String> template;

    public void sendNewProject(Collection<Long> userIds, Long projectId) {
        template.send("saveProjectToUser", 0, userIds.toString(), projectId.toString());
    }

    public void sendNewProjectToNotification(Long projectId, String users) {
        template.send("notificationHandler", 1, projectId.toString(), users);
    }
}
