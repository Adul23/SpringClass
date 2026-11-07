package com.example.spring_class.task.domain.repository;

import com.example.spring_class.task.domain.model.Project;

import java.util.Optional;

public interface ProjectRepository {

    Optional<Project> findById(long id);

    Project save(Project project);
}