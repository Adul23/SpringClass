package com.example.spring_class.controller;

import com.example.spring_class.config.AppConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final AppConfig properties;

    public ConfigController(AppConfig properties) {
        this.properties = properties;
    }

    @GetMapping
    public AppConfig getConfig() {
        return properties;
    }
}