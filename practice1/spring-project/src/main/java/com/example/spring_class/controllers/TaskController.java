package com.example.spring_class.controllers;

import com.example.spring_class.dto.TaskRequest;
import com.example.spring_class.dto.TaskResponse;
import com.example.spring_class.models.Task;
import com.example.spring_class.services.TaskService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Page<TaskResponse> findAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return taskService.findAll(pageable)
                .map(TaskResponse::from);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable long id) {
        Task task = taskService.findById(id);
        return TaskResponse.from(task);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody TaskRequest request) {

        Task task = taskService.create(request);

        return ResponseEntity
                .created(URI.create("/api/tasks/" + task.getId()))
                .body(TaskResponse.from(task));
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable long id,
            @Valid @RequestBody TaskRequest request) {

        Task task = taskService.update(id, request);
        return TaskResponse.from(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}