package com.project.notificationService.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NotificationEvent {
    private String eventType;
    private String username;
    private String email;
    private Long taskId;
    private Long projectId;
    private String title;
    private LocalDate deadline;
}
