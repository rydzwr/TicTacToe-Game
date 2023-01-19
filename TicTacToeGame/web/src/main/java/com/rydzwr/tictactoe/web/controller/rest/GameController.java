package com.rydzwr.tictactoe.web.controller.rest;

import com.rydzwr.tictactoe.database.dto.GameBoardDto;
import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.LoadGameDto;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.validator.GameDtoValidator;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;
    private final GameDtoValidator validator;

    @GetMapping("/canResumeGame")
    @PreAuthorize("hasAuthority('USER')")
    public boolean isUserInGame() {
        return gameService.isUserInGame();
    }

    @GetMapping("/continueGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> continueGame() {
        if (!gameService.isUserInGame()) {
            return new ResponseEntity<>(WebConstants.PREV_GAME_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        LoadGameDto loadedGame = gameService.loadPreviousPlayerGame();
        log.info("LOADED GAME: --> {}", loadedGame);
        return new ResponseEntity<>(loadedGame, HttpStatus.OK);
    }

    @PostMapping("/createGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> createNewGame(@Valid @RequestBody GameDto gameDto) {
        if (gameService.isUserInGame()) {
            String callerName = SecurityContextHolder.getContext().getAuthentication().getName();
            gameService.removePrevUserGame(callerName);
        }

        if (!validator.validateReceivedData(gameDto)) {
            return new ResponseEntity<>(WebConstants.GAME_VALIDATOR_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
        }
        gameService.buildGame(gameDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
