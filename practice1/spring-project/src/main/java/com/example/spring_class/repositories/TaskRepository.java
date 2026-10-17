package com.example.spring_class.repositories;

import com.example.spring_class.dto.TaskRequest;
import com.example.spring_class.exceptions.ResourceNotFoundException;
import com.example.spring_class.models.Task;
import com.example.spring_class.models.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    /*
    findById(id)	Optional<Task> — задача может отсутствовать
    findAll()	Все задачи
    findAll(pageable)	Страница задач с сортировкой
    save(task)	Сохранение entity
    delete(task)	Удаление entity
     */
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    @Query("""
        select t from Task t
        where lower(t.title) like lower(concat('%', :title, '%'))
        """)
    Page<Task> findByTitleContaining(
            @Param("title") String title,
            Pageable pageable
    );

}
