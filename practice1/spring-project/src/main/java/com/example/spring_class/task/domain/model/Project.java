package com.example.spring_class.task.domain.model;

public class Project {
    private final Long id;
    private String name;
    public Project(Long id, String name){
        validateName(name);
        this.id = id;
        this.name = name;
    }
    public static Project create(String name){
        return new Project(null, name);
    }
    public void rename(String name){
        validateName(name);
        this.name = name;
    }
    private static void validateName(String name){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException(
                    "Project name must not be blank"
            );
        }
        if (name.length() > 100){
            throw new IllegalArgumentException(
                    "Project name must not exceed 100 characters"
            );
        }
    }
    public String getName(){
        return name;
    }
    public Long getId(){
        return id;
    }
}