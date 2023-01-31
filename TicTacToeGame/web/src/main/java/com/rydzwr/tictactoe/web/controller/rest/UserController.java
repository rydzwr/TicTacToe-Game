package com.rydzwr.tictactoe.web.controller.rest;

import com.rydzwr.tictactoe.service.dto.incoming.UserDto;
import com.rydzwr.tictactoe.service.security.user.UserService;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody UserDto userDto) {
        userService.createUser(userDto);
        return new ResponseEntity<>(WebConstants.USER_HAS_BEEN_CREATED, HttpStatus.CREATED);
    }
}
