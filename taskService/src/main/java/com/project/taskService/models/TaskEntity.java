package com.project.taskService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "status")
    private String status; //TO_DO, IN_PROGRESS, DONE

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @CollectionTable(name = "task_member",
                    joinColumns = @JoinColumn(name = "task_id")
    )
    @Column(name = "user_id")
    private Long memberId;

    @Column(name = "project_id")
    private Long projectId;
}
