package com.example.spring_class.models;

public record Task(long id, String title, String description, TaskStatus status) {
}