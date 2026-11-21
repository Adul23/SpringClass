package com.example.spring_class.task.infra.holiday;

import java.time.LocalDate;

public interface HolidayChecker {
    HolidayStatus check(LocalDate date);
}
