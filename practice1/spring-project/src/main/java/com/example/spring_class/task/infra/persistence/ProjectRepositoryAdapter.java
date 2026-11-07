package com.example.spring_class.task.infra.persistence;

import com.example.spring_class.task.domain.model.Project;
import com.example.spring_class.task.domain.repository.ProjectRepository;
import com.example.spring_class.exceptions.ResourceNotFoundException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final JpaProjectRepository projects;

    public ProjectRepositoryAdapter(JpaProjectRepository projects) {
        this.projects = projects;
    }

    @Override
    public Optional<Project> findById(long id) {
        return projects.findById(id)
                .map(ProjectPersistenceMapper::toDomain);
    }
    
    @Override
    @Transactional
    public Project save(Project project) {
        ProjectEntity entity;

        if (project.getId() == null) {
            entity = new ProjectEntity();
        } else {
            entity = projects.findById(project.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Project " + project.getId()
                                    + " was not found"
                    ));
        }

        ProjectPersistenceMapper.copyToEntity(project, entity);

        return ProjectPersistenceMapper.toDomain(
                projects.save(entity)
        );
    }
}