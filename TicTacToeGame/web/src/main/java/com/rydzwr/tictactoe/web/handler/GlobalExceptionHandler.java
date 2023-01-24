package com.rydzwr.tictactoe.web.handler;

import com.rydzwr.tictactoe.web.dto.ErrorModelDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
/*
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> handleMyCustomException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ErrorModelDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

 */
}

