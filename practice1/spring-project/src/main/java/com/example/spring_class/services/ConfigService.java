package com.example.spring_class.services;

import com.example.spring_class.config.AppProperties;
import org.springframework.stereotype.Service;

@Service
public class ConfigService{
    private final AppProperties appProperties;
    public ConfigService(AppProperties appProperties){
        this.appProperties = appProperties;
    }
    public String getMessage(){
        return appProperties.message();
    }
    public String getApplicationName(){
        return appProperties.name();
    }

    public AppProperties getAppProperties() {
        return appProperties;
    }
}