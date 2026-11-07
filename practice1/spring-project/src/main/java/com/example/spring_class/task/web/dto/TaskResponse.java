package com.example.spring_class.task.web.dto;

import com.example.spring_class.task.domain.model.TaskStatus;

public record TaskResponse(
        long id,
        String title,
        String description,
        TaskStatus status
) {
}