package com.example.spring_class.task.domain.repository;

import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaskRepository {

    Optional<Task> findById(long id);

    Page<Task> findAll(Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByTitleContaining(String title, Pageable pageable);

    Task save(Task task);

    void delete(Task task);
}