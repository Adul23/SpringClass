package com.example.spring_class.controllers;

import com.example.spring_class.config.AppProperties;
import com.example.spring_class.services.ConfigService;
import com.example.spring_class.services.DebugService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService configService;
    private final Optional<DebugService> debugService;

    public ConfigController(ConfigService configService, Optional<DebugService> debugService) {
        this.configService = configService;
        this.debugService = debugService;
    }

    @GetMapping("/properties")
    public AppProperties getConfig() {
        return configService.getAppProperties();
    }

    @GetMapping("/name")
    public String getAppName() {
        return configService.getApplicationName();
    }

    @GetMapping("/message")
    public String getMessage() {
        return configService.getMessage();
    }
    @GetMapping("/debugmessage")
    public String getDebugMessage(){
        return debugService.map(DebugService::debugMessage).orElse("Debug Mode is disabled");
    }
}