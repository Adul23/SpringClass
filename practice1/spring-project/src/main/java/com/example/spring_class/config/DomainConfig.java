package com.example.spring_class.config;

import com.example.spring_class.task.domain.repository.ProjectRepository;
import com.example.spring_class.task.domain.repository.TaskRepository;
import com.example.spring_class.task.domain.service.TaskDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig{
    @Bean
    public TaskDomainService taskDomainService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository
    ){
        return new TaskDomainService(taskRepository, projectRepository);
    }
}