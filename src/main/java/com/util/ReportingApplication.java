package com.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileNotFoundException;

@SpringBootApplication
@EntityScan(basePackages = "com.entity")
@EnableJpaRepositories(basePackages = "com.repository")
@ComponentScan(basePackages = {"com.util", "com.service", "com.controller", "com.kafka"}) // Add "com.kafka" here
public class ReportingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportingApplication.class, args);
	}
}