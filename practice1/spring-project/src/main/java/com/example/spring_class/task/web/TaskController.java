package com.example.spring_class.task.web;

import com.example.spring_class.task.application.service.TaskService;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.task.web.dto.TaskRequest;
import com.example.spring_class.task.web.dto.TaskResponse;
import com.example.spring_class.task.web.mapper.TaskWebMapper;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "title", "description", "status");

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public Page<TaskResponse> findAll(
            @PageableDefault(size = 10, sort = "id")
            Pageable pageable
    ) {
        return taskService.findAll(normalize(pageable))
                .map(TaskWebMapper::toResponse);
    }

    @GetMapping("/by-status")
    public Page<TaskResponse> findByStatus(
            @RequestParam TaskStatus status,
            @PageableDefault(size = 10, sort = "id")
            Pageable pageable
    ) {
        return taskService.findByStatus(status, normalize(pageable))
                .map(TaskWebMapper::toResponse);
    }

    @GetMapping("/search")
    public Page<TaskResponse> search(
            @RequestParam String title,
            @PageableDefault(size = 10, sort = "id")
            Pageable pageable
    ) {
        return taskService.searchByTitle(title, normalize(pageable))
                .map(TaskWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable long id) {
        return TaskWebMapper.toResponse(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody TaskRequest request
    ) {
        Task saved = taskService.create(
                TaskWebMapper.toDomain(request)
        );

        return ResponseEntity
                .created(URI.create("/api/tasks/" + saved.getId()))
                .body(TaskWebMapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable long id,
            @Valid @RequestBody TaskRequest request
    ) {
        Task updated = taskService.update(
                id,
                request.title(),
                request.description(),
                request.status(),
                request.projectId()
        );

        return TaskWebMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Pageable normalize(Pageable pageable) {
        Sort sort = pageable.getSort();

        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException(
                        "Unsupported sort field: " + order.getProperty()
                );
            }
        }

        // ID делает порядок устойчивым при одинаковых названиях.
        sort.getOrderFor("id");

        return PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 100),
                sort
        );
    }
}