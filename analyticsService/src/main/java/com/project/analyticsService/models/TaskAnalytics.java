package com.project.analyticsService.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@Document
public class TaskAnalytics {
    @Id
    private String id;
    private Long taskId;
    private Long projectId;
    private Long userId;
    private LocalDate completionTime;
    private String status;
}
