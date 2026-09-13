package com.example.spring_class;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringClassApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringClassApplication.class, args);
	}
}