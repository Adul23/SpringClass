package com.example.spring_class.services;

import com.example.spring_class.dto.TaskRequest;
import com.example.spring_class.exceptions.ResourceNotFoundException;
import com.example.spring_class.models.Project;
import com.example.spring_class.models.Task;
import com.example.spring_class.repositories.ProjectRepository;
import com.example.spring_class.repositories.TaskRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Task findById(long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> missing(id));
    }

    @Transactional
    public Task create(TaskRequest request) {
        Task task = new Task();
        fillFields(task, request);

        return taskRepository.save(task);
    }

    @Transactional
    public Task update(long id, TaskRequest request) {
        Task task = findById(id);
        fillFields(task, request);

        return taskRepository.save(task);
    }

    @Transactional
    public void delete(long id) {
        Task task = findById(id);
        taskRepository.delete(task);
    }

    private void fillFields(Task task, TaskRequest request) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());

        if (request.projectId() == null) {
            task.setProject(null);
        } else {
            Project project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Project " + request.projectId() + " was not found"
                    ));

            task.setProject(project);
        }
    }

    private ResourceNotFoundException missing(long id) {
        return new ResourceNotFoundException(
                "Task " + id + " was not found"
        );
    }
}