package com.rydzwr.tictactoe.database.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorModelDto {
    private String message;
}
