package com.project.taskService.api.DTOs;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String body;
    private String status; //TO_DO, IN_PROGRESS, DONE
    private LocalDate deadline;
    private Long memberId;
    private Long projectId;
}
