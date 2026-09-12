package com.example.spring_class.services;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class ExampleService {
    public DayOfWeek getDayOfTheWeek(LocalDate date){
        return date.getDayOfWeek();
    }
}
