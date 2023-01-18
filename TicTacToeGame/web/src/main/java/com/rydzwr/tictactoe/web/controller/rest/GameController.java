package com.rydzwr.tictactoe.web.controller.rest;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.game.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    @GetMapping("/canResumeGame")
    @PreAuthorize("hasAuthority('USER')")
    public boolean isUserInGame() {
        return gameService.isUserInGame();
    }

    @PostMapping("/createGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> createNewGame(@Valid @RequestBody GameDto gameDto) {
        gameService.buildGame(gameDto);
        return ResponseEntity.ok().build();
    }
}
