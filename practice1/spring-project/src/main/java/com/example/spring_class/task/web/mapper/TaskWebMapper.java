package com.example.spring_class.task.web.mapper;

import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.web.dto.TaskRequest;
import com.example.spring_class.task.web.dto.TaskResponse;

public final class TaskWebMapper {

    private TaskWebMapper() {
    }

    public static Task toDomain(TaskRequest request) {
        return Task.create(
                request.title(),
                request.description(),
                request.status(),
                request.projectId()
        );
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus()
        );
    }
}