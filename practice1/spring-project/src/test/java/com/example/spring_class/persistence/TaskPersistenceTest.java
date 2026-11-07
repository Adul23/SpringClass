package com.example.spring_class.persistence;


import com.example.spring_class.task.domain.model.Project;
import com.example.spring_class.task.domain.model.Task;
import com.example.spring_class.task.domain.model.TaskStatus;
import com.example.spring_class.task.domain.repository.ProjectRepository;
import com.example.spring_class.task.domain.repository.TaskRepository;
import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=${TEST_DB_URL}",
                "spring.datasource.username=${TEST_DB_USERNAME}",
                "spring.datasource.password=${TEST_DB_PASSWORD}",
                "spring.jpa.hibernate.ddl-auto=validate",
                "spring.sql.init.mode=never",
                "spring.flyway.enabled=true",
                "spring.flyway.baseline-on-migrate=false",
                "spring.flyway.locations=classpath:db/migration"
        }
)
@ActiveProfiles("test")
@Transactional
class TaskPersistenceTest {

    @Autowired
    TaskRepository tasks;

    @Autowired
    ProjectRepository projects;

    @Autowired
    EntityManager entityManager;

    @Test
    void savesReadsQueriesUpdatesAndDeletesTask() {
        String title = "SIS2-" + UUID.randomUUID();

        Project project = projects.save(Project.create("SIS2 Test"));

        Task saved = tasks.save(Task.create(
                title,
                "Repository integration test",
                TaskStatus.TODO,
                project.getId()
        ));

        assertNotNull(project.getId());
        assertNotNull(saved.getId());

        entityManager.flush();
        entityManager.clear();

        Task loaded = tasks.findById(saved.getId()).orElseThrow();

        assertEquals(title, loaded.getTitle());
        assertEquals(project.getId(), loaded.getProjectId());

        var searched = tasks.findByTitleContaining(
                title.toLowerCase(),
                PageRequest.of(0, 10, Sort.by("id"))
        );

        assertEquals(1L, searched.getTotalElements());
        assertEquals(saved.getId(), searched.getContent().get(0).getId());

        var byStatus = tasks.findByStatus(
                TaskStatus.TODO,
                PageRequest.of(0, 100, Sort.by("id").descending())
        );

        assertTrue(byStatus.getContent().stream()
                .allMatch(task -> task.getStatus() == TaskStatus.TODO));

        assertTrue(byStatus.getContent().stream()
                .anyMatch(task -> task.getId().equals(saved.getId())));

        loaded.update(title, "Changed", TaskStatus.DONE, project.getId());
        tasks.save(loaded);

        entityManager.flush();
        entityManager.clear();

        Task updated = tasks.findById(saved.getId()).orElseThrow();

        assertEquals(saved.getId(), updated.getId());
        assertEquals(TaskStatus.DONE, updated.getStatus());
        assertEquals("Changed", updated.getDescription());

        tasks.delete(updated);

        entityManager.flush();
        entityManager.clear();

        assertTrue(tasks.findById(saved.getId()).isEmpty());
    }
}