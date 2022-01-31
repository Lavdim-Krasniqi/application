package com.securutyExamples.application;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@ComponentScan
@EnableReactiveMongoRepositories
public class ApiConfiguration {
}
