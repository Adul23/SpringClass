package com.example.spring_class.task.infra.holiday;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class NagerDateClient {
    private final RestClient restClient;
    public NagerDateClient(
            RestClient.Builder builder,
            @Value("${holidays.base-url}") String baseUrl
    ){
        this.restClient = builder.baseUrl(baseUrl).build();
    }
    @Retryable(
            includes = {ResourceAccessException.class, HttpServerErrorException.class},
            maxRetries = 2,
            delay = 200,
            multiplier = 2,
            jitter = 50
    )
    public List<PublicHoliday> publicHolidays(int year, String countryCode){
        PublicHoliday[] body = restClient.get()
                .uri("api/v3/PublicHolidays/{year}/{country}", year, countryCode)
                .retrieve()
                .body(PublicHoliday[].class);
        return body == null ? List.of() : List.of(body);
    }

}
