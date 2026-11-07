package com.example.spring_class.task.infra.persistence;

import com.example.spring_class.task.domain.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaTaskRepository
        extends JpaRepository<TaskEntity, Long> {

    Page<TaskEntity> findByStatus(
            TaskStatus status,
            Pageable pageable
    );

    @Query("""
            select t from TaskEntity t
            where lower(t.title) like lower(concat('%', :title, '%'))
            """)
    Page<TaskEntity> findByTitleContaining(
            @Param("title") String title,
            Pageable pageable
    );
}