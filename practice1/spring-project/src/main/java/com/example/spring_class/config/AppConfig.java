package com.example.spring_class.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppConfig(
        String name,
        String message,
        int maxUsers,
        boolean debugEnabled
) {
}