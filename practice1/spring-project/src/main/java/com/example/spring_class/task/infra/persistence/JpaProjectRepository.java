package com.example.spring_class.task.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProjectRepository
        extends JpaRepository<ProjectEntity, Long> {
}