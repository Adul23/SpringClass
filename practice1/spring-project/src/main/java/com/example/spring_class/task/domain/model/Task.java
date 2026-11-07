package com.example.spring_class.task.domain.model;

public class Task {

    private final Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long projectId;

    public Task(
            Long id,
            String title,
            String description,
            TaskStatus status,
            Long projectId
    ) {
        validate(title, description, status);

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.projectId = projectId;
    }

    public static Task create(
            String title,
            String description,
            TaskStatus status,
            Long projectId
    ) {
        return new Task(null, title, description, status, projectId);
    }

    public void update(
            String title,
            String description,
            TaskStatus status,
            Long projectId
    ) {
        validate(title, description, status);

        this.title = title;
        this.description = description;
        this.status = status;
        this.projectId = projectId;
    }

    public void changeStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Task status is required");
        }

        this.status = status;
    }

    private static void validate(
            String title,
            String description,
            TaskStatus status
    ) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title must not be blank");
        }

        if (title.length() > 100) {
            throw new IllegalArgumentException(
                    "Task title must not exceed 100 characters"
            );
        }

        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException(
                    "Task description must not exceed 1000 characters"
            );
        }

        if (status == null) {
            throw new IllegalArgumentException("Task status is required");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Long getProjectId() {
        return projectId;
    }
}