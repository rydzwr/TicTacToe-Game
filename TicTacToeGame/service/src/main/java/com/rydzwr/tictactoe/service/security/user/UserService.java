package com.rydzwr.tictactoe.service.security.user;

import com.rydzwr.tictactoe.service.security.database.UserDatabaseService;
import com.rydzwr.tictactoe.service.dto.incoming.UserDto;
import com.rydzwr.tictactoe.service.security.factory.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDatabaseService userDatabaseService;
    private final UserFactory userFactory;
    public void createUser(UserDto userDto) {
        var newUser = userFactory.createUser(userDto.getName(), userDto.getPassword());
        userDatabaseService.saveUser(newUser);
    }
}
