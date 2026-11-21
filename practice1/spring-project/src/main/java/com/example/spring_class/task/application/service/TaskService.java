package com.example.spring_class.task.application.service;

import com.example.spring_class.exceptions.ResourceNotFoundException;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.task.domain.repository.TaskRepository;
import com.example.spring_class.task.infra.holiday.HolidayChecker;
import com.example.spring_class.task.infra.holiday.HolidayStatus;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.spring_class.task.domain.repository.ProjectRepository;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "tasks")
public class TaskService {
    public static final String TASK_CACHE = "task";
    public static final String TASK_PAGES_CACHE = "taskPages";
    private final TaskRepository tasks;
    private final HolidayChecker holidayChecker;

    private final ProjectRepository projects;
    public TaskService(
            TaskRepository tasks,
            ProjectRepository projects,
            HolidayChecker holidayChecker
    ){
        this.tasks = tasks;
        this.projects = projects;
        this.holidayChecker = holidayChecker;
    }

    @Cacheable(cacheNames = TASK_PAGES_CACHE, key = "'all:' + #pageable")
    public Page<Task> findAll(Pageable pageable){
        return tasks.findAll(pageable);
    }
    @Cacheable(cacheNames = TASK_PAGES_CACHE, key = "'status:' + #status + ':' + #pageable")
    public Page<Task> findByStatus(
            TaskStatus status,
            Pageable pageable
    ){
        return tasks.findByStatus(status, pageable);
    }
    @Cacheable(cacheNames = TASK_PAGES_CACHE, key = "'title:' + #title + ':' + #pageable")
    public Page<Task> searchByTitle(
            String title,
            Pageable pageable
    ) {
        return tasks.findByTitleContaining(title, pageable);
    }
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = TASK_CACHE, key = "#result.id"),
            evict = @CacheEvict(cacheNames = TASK_PAGES_CACHE, allEntries = true)
    )
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
    @Caching(
            put = @CachePut(cacheNames = TASK_CACHE, key = "#id"),
            evict = @CacheEvict(cacheNames = TASK_PAGES_CACHE, allEntries = true)
    )
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
    @Caching(evict = {
            @CacheEvict(cacheNames = TASK_CACHE, key = "#id"),
            @CacheEvict(cacheNames = TASK_PAGES_CACHE, allEntries = true)
    })
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
    @Cacheable(cacheNames = TASK_CACHE, key = "#id")
    public Task findById(long id){
        return requireTask(id);
    }

    private Task requireTask(long id) {
        return tasks.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException(
                        "Task " + id + " was not found"
                ));
    }
    public HolidayStatus checkHoliday(LocalDate date) {
        return holidayChecker.check(date);
    }
}