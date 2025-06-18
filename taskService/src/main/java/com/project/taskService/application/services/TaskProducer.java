package com.project.taskService.application.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskProducer {
    private final KafkaTemplate<String, String> template;

    public void saveTaskToProject(Long projectId, Long taskId, Set<Long> members) {
        template.send("saveTaskToProject", 0, taskId.toString(), projectId.toString());
        template.send("saveTaskToProject", 1, taskId.toString(), members.toString());
    }
}
