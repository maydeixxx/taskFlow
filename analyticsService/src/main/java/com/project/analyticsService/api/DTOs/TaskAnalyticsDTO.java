package com.project.analyticsService.api.DTOs;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TaskAnalyticsDTO {
    private String id;
    private Long taskId;
    private Long projectId;
    private Long userId;
    private ZonedDateTime completionTime;
    private String status;
}
