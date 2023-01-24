package com.rydzwr.tictactoe.database.dto.incoming;

import com.rydzwr.tictactoe.database.validator.user.UniqueLogin;
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
