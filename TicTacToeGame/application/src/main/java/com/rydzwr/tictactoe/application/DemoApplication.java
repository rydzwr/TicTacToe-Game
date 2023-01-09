package com.rydzwr.tictactoe.application;

import com.rydzwr.tictactoe.security.MyService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.rydzwr.tictactoe")
@RestController
public class DemoApplication {

    private final MyService myService;

    public DemoApplication(MyService myService) {
        this.myService = myService;
    }

    @GetMapping("/")
    public String home() {
        return myService.getMessage();
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}