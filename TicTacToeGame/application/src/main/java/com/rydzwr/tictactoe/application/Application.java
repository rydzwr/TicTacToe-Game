package com.rydzwr.tictactoe.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@SpringBootApplication
@EnableAutoConfiguration
@EntityScan(basePackages="com.rydzwr.tictactoe")
@ComponentScan(basePackages="com.rydzwr.tictactoe")
@EnableJpaRepositories(basePackages="com.rydzwr.tictactoe")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}