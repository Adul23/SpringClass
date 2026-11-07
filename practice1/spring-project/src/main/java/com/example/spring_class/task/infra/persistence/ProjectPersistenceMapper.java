package com.example.spring_class.task.infra.persistence;

import com.example.spring_class.task.domain.model.Project;

public final class ProjectPersistenceMapper {

    private ProjectPersistenceMapper() {
    }

    public static Project toDomain(ProjectEntity entity) {
        return new Project(entity.getId(), entity.getName());
    }

    public static void copyToEntity(
            Project project,
            ProjectEntity entity
    ) {
        entity.setName(project.getName());
    }
}