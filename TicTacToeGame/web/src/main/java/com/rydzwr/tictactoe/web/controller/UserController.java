package com.rydzwr.tictactoe.web.controller;

import com.rydzwr.tictactoe.database.dto.UserDto;
import com.rydzwr.tictactoe.database.factory.UserFactory;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService service;
    private final UserFactory factory;

    @PostMapping("/register")
    public ResponseEntity<String> register (@Valid @RequestBody UserDto appUserDto) {
        User newUser = factory.createUser(appUserDto.getName(), appUserDto.getPassword());
        service.saveUser(newUser);
        return ResponseEntity.ok().build();
    }
}
