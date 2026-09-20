package com.example.spring_class.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.spring_class.services.DebugService;

@Configuration
public class DebugConfig {

    @Bean
    @ConditionalOnProperty(
            name = "app.debug-enabled",
            havingValue = "true"
    )
    public DebugService debugService() {
        return new DebugService();
    }
}