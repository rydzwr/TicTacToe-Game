package com.rydzwr.tictactoe.service.dto.incoming;

import com.rydzwr.tictactoe.service.security.validator.UniqueLogin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    @UniqueLogin
    @Size(min = 3, max = 20)
    private String name;
    @Size(min = 3, max = 20)
    private String password;
}
