package com.example.content_calendar;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching
public class ContentCalendarApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ContentCalendarApplication.class, args);
		Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
	}


}
