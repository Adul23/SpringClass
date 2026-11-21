package com.example.spring_class.task.infra.holiday;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

@Component
public class HolidayCheckerAdapter implements HolidayChecker{
    private static final Logger log = LoggerFactory.getLogger(HolidayCheckerAdapter.class);
    private static final String COUNTRY = "KZ";
    private final NagerDateClient nagerDateClient;
    public HolidayCheckerAdapter(NagerDateClient nagerDateClient) {
        this.nagerDateClient = nagerDateClient;
    }

    @Override
    public HolidayStatus check(LocalDate date) {
        try {
            boolean holiday = nagerDateClient.publicHolidays(date.getYear(), COUNTRY).stream()
                    .anyMatch(h -> h.date().equals(date));
            if (holiday){
                return HolidayStatus.HOLIDAY;
            }
            return HolidayStatus.WORKING_DAY;
        } catch(RestClientException ex){
            log.warn("Holiday service unavailable, falling back to UNKNOWN: {}", ex.getMessage());
            return HolidayStatus.UKNOWN;
        }
    }


}
