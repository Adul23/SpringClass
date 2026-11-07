package com.example.spring_class.task.infra.persistence;

import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.repository.TaskRepository;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.exceptions.ResourceNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class TaskRepositoryAdapter implements TaskRepository {

    private final JpaTaskRepository tasks;
    private final JpaProjectRepository projects;

    public TaskRepositoryAdapter(
            JpaTaskRepository tasks,
            JpaProjectRepository projects
    ) {
        this.tasks = tasks;
        this.projects = projects;
    }

    @Override
    public Optional<Task> findById(long id) {
        return tasks.findById(id)
                .map(TaskPersistenceMapper::toDomain);
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        return tasks.findAll(pageable)
                .map(TaskPersistenceMapper::toDomain);
    }

    @Override
    public Page<Task> findByStatus(
            TaskStatus status,
            Pageable pageable
    ) {
        return tasks.findByStatus(status, pageable)
                .map(TaskPersistenceMapper::toDomain);
    }

    @Override
    public Page<Task> findByTitleContaining(
            String title,
            Pageable pageable
    ) {
        return tasks.findByTitleContaining(title, pageable)
                .map(TaskPersistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public Task save(Task task) {
        TaskEntity entity;

        if (task.getId() == null) {
            entity = new TaskEntity();
        } else {
            entity = tasks.findById(task.getId())
                    .orElseThrow(() -> missingTask(task.getId()));
        }

        ProjectEntity project = null;

        if (task.getProjectId() != null) {
            project = projects.findById(task.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Project " + task.getProjectId()
                                    + " was not found"
                    ));
        }

        TaskPersistenceMapper.copyToEntity(task, entity, project);

        TaskEntity saved = tasks.save(entity);

        return TaskPersistenceMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(Task task) {
        TaskEntity entity = tasks.findById(task.getId())
                .orElseThrow(() -> missingTask(task.getId()));

        tasks.delete(entity);
    }

    private ResourceNotFoundException missingTask(long id) {
        return new ResourceNotFoundException(
                "Task " + id + " was not found"
        );
    }
}