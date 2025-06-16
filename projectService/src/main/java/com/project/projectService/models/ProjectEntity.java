package com.project.projectService.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Collection;

@Entity
@Data
@Table(name = "projects")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "status")
    private String status; //STARTED, NOT_STARTED, DONE

    @ElementCollection
    @CollectionTable(name = "project_members",
            joinColumns = @JoinColumn(name = "project_id")
    )
    @Column(name = "user_id")
    private Collection<Long> members;
}
