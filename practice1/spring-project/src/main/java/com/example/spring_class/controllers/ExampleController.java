package com.example.spring_class.controllers;

import com.example.spring_class.services.ExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;


@RestController
public class ExampleController{
    private final ExampleService exampleService;
    ExampleController(ExampleService exampleService){
        this.exampleService = exampleService;
    }
    @GetMapping("/dateOfWeek")
    public DayOfWeek getDate(@RequestParam LocalDate date){
        return exampleService.getDayOfTheWeek(date);
    }

}