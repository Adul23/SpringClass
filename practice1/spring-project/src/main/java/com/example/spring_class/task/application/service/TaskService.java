package com.example.spring_class.task.application.service;

import com.example.spring_class.exceptions.ResourceNotFoundException;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.task.domain.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.spring_class.task.domain.repository.ProjectRepository;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository tasks;
    private final ProjectRepository projects;
    public TaskService(
            TaskRepository tasks,
            ProjectRepository projects
    ){
        this.tasks = tasks;
        this.projects = projects;
    }
    public Page<Task> findAll(Pageable pageable){
        return tasks.findAll(pageable);
    }
    public Page<Task> findByStatus(
            TaskStatus status,
            Pageable pageable
    ){
        return tasks.findByStatus(status, pageable);
    }
    public Page<Task> searchByTitle(
            String title,
            Pageable pageable
    ) {
        return tasks.findByTitleContaining(title, pageable);
    }

    @Transactional
    public Task create(Task task){
        if (task.getId() != null){
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
    ){
        Task task = requireTask(id);
        requireProject(projectId);
        task.update(title, description, status, projectId);
        return tasks.save(task);
    }

    @Transactional
    public void delete(
            long id
    ){
        Task task = requireTask(id);
        tasks.delete(task);

    }

    private void requireProject(Long projectId) {
        if (projectId == null) {
            return;
        }
        projects.findById(projectId)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "Project " + projectId + " was not found"
                ));
    }

    public Task findById(long id){
        return requireTask(id);
    }

    private Task requireTask(long id) {
        return tasks.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "Task " + id + " was not found"
                ));
    }

}