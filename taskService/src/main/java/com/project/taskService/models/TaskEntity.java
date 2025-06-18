package com.project.taskService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
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
    private LocalDate deadline;

    @ElementCollection
    @CollectionTable(name = "task_members",
                    joinColumns = @JoinColumn(name = "task_id")
    )
    @Column(name = "user_id")
    private Set<Long> members;

    @Column(name = "project_id")
    private Long projectId;
}
