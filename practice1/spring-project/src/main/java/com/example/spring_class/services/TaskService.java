package com.example.spring_class.services;

import com.example.spring_class.dto.TaskRequest;
import com.example.spring_class.exceptions.ResourceNotFoundException;
import com.example.spring_class.models.Task;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparingLong(Task::id))
                .toList();
    }

    public Task findById(long id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw missing(id);
        }
        return task;
    }

    public Task create(TaskRequest request) {
        long id = sequence.incrementAndGet();
        Task task = new Task(
                id, request.title(), request.description(), request.status()
        );
        tasks.put(id, task);
        return task;
    }

    public Task update(long id, TaskRequest request) {
        return tasks.compute(id, (key, existing) -> {
            if (existing == null) {
                throw missing(id);
            }
            return new Task(
                    id, request.title(), request.description(), request.status()
            );
        });
    }

    public void delete(long id) {
        if (tasks.remove(id) == null) {
            throw missing(id);
        }
    }

    private ResourceNotFoundException missing(long id) {
        return new ResourceNotFoundException("Task " + id + " was not found");
    }
}