package com.example.spring_class.task.web;

import com.example.spring_class.task.application.service.TaskService;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.task.infra.holiday.HolidayChecker;
import com.example.spring_class.task.infra.holiday.HolidayCheckerAdapter;
import com.example.spring_class.task.infra.holiday.HolidayStatus;
import com.example.spring_class.task.web.dto.TaskRequest;
import com.example.spring_class.task.web.dto.TaskResponse;
import com.example.spring_class.task.web.error.ApiError;
import com.example.spring_class.task.web.mapper.TaskWebMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api/tasks")

@Tag(name = "Tasks", description = "This REST Controller for the Tasks")
public class TaskController {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "title", "description", "status");

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @Operation(summary="API for finding all tasks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All task found"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })

    @GetMapping
    public Page<TaskResponse> findAll(
            @PageableDefault(size = 10, sort = "id")
            Pageable pageable
    ) {
        return taskService.findAll(normalize(pageable))
                .map(TaskWebMapper::toResponse);
    }

    @Operation(summary="API for finding tasks by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task by status found"),
            @ApiResponse(responseCode = "404", description = "Task by status not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })

    @GetMapping("/by-status")
    public Page<TaskResponse> findByStatus(
            @RequestParam TaskStatus status,
            @PageableDefault(size = 10, sort = "id")
            Pageable pageable
    ) {
        return taskService.findByStatus(status, normalize(pageable))
                .map(TaskWebMapper::toResponse);
    }

    @Operation(summary="API for searching tasks by title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task by title found"),
            @ApiResponse(responseCode = "404", description = "Task by title not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })

    @GetMapping("/search")
    public Page<TaskResponse> search(
            @RequestParam String title,
            @PageableDefault(size = 10, sort = "id")
            Pageable pageable
    ) {
        return taskService.searchByTitle(title, normalize(pageable))
                .map(TaskWebMapper::toResponse);
    }

    @Operation(summary="API for finding tasks by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task by id found"),
            @ApiResponse(responseCode = "404", description = "Task by id not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })

    @GetMapping("/{id}")

    public TaskResponse findById(@PathVariable long id) {
        return TaskWebMapper.toResponse(taskService.findById(id));
    }

    @Operation(summary="API for creating task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task was created found"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary="API for updating task via id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task was updated"),
            @ApiResponse(responseCode = "404", description = "Task was not updated and not found",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })

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

    @Operation(summary="API for deleting task by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found and was not deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary="Just checking whether date is holiday or not")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "it returns UNKNOWN if the holiday service is unavailable. This documents your fallback."),
            @ApiResponse(responseCode = "400", description = "ApiError, bad format",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/holiday-check")
    public HolidayStatus getHolidayOrNot(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return taskService.checkHoliday(date);
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

        sort.getOrderFor("id");

        return PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), 100),
                sort
        );
    }
}