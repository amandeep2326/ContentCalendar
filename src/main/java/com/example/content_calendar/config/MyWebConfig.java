package com.example.content_calendar.config;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
@Configuration
public class MyWebConfig {
    @Bean
    // Indicates that a method produces a bean to be managed by Spring Container
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
