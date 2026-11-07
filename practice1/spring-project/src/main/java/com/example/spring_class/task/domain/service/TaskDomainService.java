package com.example.spring_class.task.domain.service;

import com.example.spring_class.task.domain.repository.ProjectRepository;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.repository.TaskRepository;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.exceptions.ResourceNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


public class TaskDomainService {

    private final TaskRepository tasks;
    private final ProjectRepository projects;

    public TaskDomainService(
            TaskRepository tasks,
            ProjectRepository projects
    ) {
        this.tasks = tasks;
        this.projects = projects;
    }

    public Page<Task> findAll(Pageable pageable) {
        return tasks.findAll(pageable);
    }

    public Page<Task> findByStatus(
            TaskStatus status,
            Pageable pageable
    ) {
        return tasks.findByStatus(status, pageable);
    }

    public Page<Task> searchByTitle(
            String title,
            Pageable pageable
    ) {
        return tasks.findByTitleContaining(title, pageable);
    }

    public Task findById(long id) {
        return requireTask(id);
    }

    @Transactional
    public Task create(Task task) {
        if (task.getId() != null) {
            throw new IllegalArgumentException(
                    "A new task must not have an ID"
            );
        }

        requireProject(task.getProjectId());

        return tasks.save(task);
    }

    @Transactional
    public Task update(
            long id,
            String title,
            String description,
            TaskStatus status,
            Long projectId
    ) {
        Task task = requireTask(id);

        requireProject(projectId);

        task.update(title, description, status, projectId);

        return tasks.save(task);
    }

    @Transactional
    public void delete(long id) {
        tasks.delete(requireTask(id));
    }

    private Task requireTask(long id) {
        return tasks.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task " + id + " was not found"
                ));
    }

    private void requireProject(Long projectId) {
        if (projectId == null) {
            return;
        }

        projects.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project " + projectId + " was not found"
                ));
    }
}