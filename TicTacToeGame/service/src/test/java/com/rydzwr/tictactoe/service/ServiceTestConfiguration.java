package com.rydzwr.tictactoe.service;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@TestConfiguration
@EnableMethodSecurity
@EnableAutoConfiguration
@EntityScan(basePackages= {"com.rydzwr.tictactoe"})
@ComponentScan(basePackages= {"com.rydzwr.tictactoe"})
@EnableJpaRepositories(basePackages= {"com.rydzwr.tictactoe"})
@SpringBootApplication(scanBasePackages = {"com.rydzwr.tictactoe"})
public class ServiceTestConfiguration {
}
