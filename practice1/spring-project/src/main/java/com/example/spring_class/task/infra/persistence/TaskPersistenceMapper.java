package com.example.spring_class.task.infra.persistence;

import com.example.spring_class.task.domain.model.Task;

public final class TaskPersistenceMapper {

    private TaskPersistenceMapper() {
    }

    public static Task toDomain(TaskEntity entity) {
        Long projectId = entity.getProject() == null
                ? null
                : entity.getProject().getId();

        return new Task(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                projectId
        );
    }

    public static void copyToEntity(
            Task task,
            TaskEntity entity,
            ProjectEntity project
    ) {
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setStatus(task.getStatus());
        entity.setProject(project);
    }
}