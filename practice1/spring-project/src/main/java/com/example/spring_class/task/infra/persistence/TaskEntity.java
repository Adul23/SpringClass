package com.example.spring_class.task.infra.persistence;

import com.example.spring_class.task.domain.model.TaskStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="title", nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 255)
    private TaskStatus status;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectEntity projectEntity;


    public TaskEntity(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public ProjectEntity getProject() {
        return projectEntity;
    }

    public void setProject(ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }
}