package com.example.spring_class.task.application;

import com.example.spring_class.exceptions.ResourceNotFoundException;

import com.example.spring_class.task.application.service.TaskService;
import com.example.spring_class.task.domain.model.Project;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.task.domain.repository.ProjectRepository;
import com.example.spring_class.task.domain.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository tasks;

    @Mock
    ProjectRepository projects;

    @InjectMocks
    TaskService service;

    @Test
    void createsTaskInExistingProject() {
        Task input = Task.create(
                "Learn Java", null, TaskStatus.TODO, 5L
        );

        when(projects.findById(5L))
                .thenReturn(Optional.of(new Project(5L, "Study")));

        when(tasks.save(input)).thenReturn(
                new Task(10L, "Learn Java", null, TaskStatus.TODO, 5L)
        );

        Task saved = service.create(input);

        assertEquals(10L, saved.getId());
        assertEquals(5L, saved.getProjectId());
        verify(tasks).save(input);
    }

    @Test
    void rejectsMissingProjectWithoutSaving() {
        Task input = Task.create(
                "Learn Java", null, TaskStatus.TODO, 99L
        );

        when(projects.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.create(input)
        );

        verify(tasks, never()).save(any(Task.class));
    }

    @Test
    void updatePreservesIdAndChangesFields() {
        Task existing = new Task(
                10L, "Old title", null, TaskStatus.TODO, null
        );

        when(tasks.findById(10L))
                .thenReturn(Optional.of(existing));

        when(tasks.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = service.update(
                10L, "New title", "Updated", TaskStatus.DONE, null
        );

        assertEquals(10L, updated.getId());
        assertEquals("New title", updated.getTitle());
        assertEquals(TaskStatus.DONE, updated.getStatus());
        verify(tasks).save(existing);
    }

    @Test
    void invalidUpdateDoesNotChangeOrSaveTask() {
        Task existing = new Task(
                10L, "Old title", null, TaskStatus.TODO, null
        );

        when(tasks.findById(10L))
                .thenReturn(Optional.of(existing));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.update(
                        10L, "", null, TaskStatus.DONE, null
                )
        );

        assertEquals("Old title", existing.getTitle());
        assertEquals(TaskStatus.TODO, existing.getStatus());
        verify(tasks, never()).save(any(Task.class));
    }

    @Test
    void missingTaskThrowsNotFound() {
        when(tasks.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(99L)
        );
    }

    @Test
    void deletesExistingTask() {
        Task existing = new Task(
                10L, "Task", null, TaskStatus.TODO, null
        );

        when(tasks.findById(10L))
                .thenReturn(Optional.of(existing));

        service.delete(10L);

        verify(tasks).delete(existing);
    }
}