package com.example.spring_class.dto;

import com.example.spring_class.models.Task;
import com.example.spring_class.models.TaskStatus;

public record TaskResponse(
        long id, String title, String description, TaskStatus status
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.id(), task.title(), task.description(), task.status()
        );
    }
}