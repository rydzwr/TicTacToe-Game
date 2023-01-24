package com.rydzwr.tictactoe.web.controller.rest;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.dto.LoadGameDto;
import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.game.service.GameService;
import com.rydzwr.tictactoe.game.validator.GameDtoValidator;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import com.rydzwr.tictactoe.web.dto.InviteCodeDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private final SimpMessagingTemplate template;

    @GetMapping("/canResumeGame")
    @PreAuthorize("hasAuthority('USER')")
    public boolean isUserInGame() {
        return gameService.isUserInGame();
    }

    @GetMapping("/inviteCode")
    @PreAuthorize("hasAuthority('USER')")
    public String getInviteCode() {
        String callerName = SecurityContextHolder.getContext().getAuthentication().getName();
        return gameService.getInviteCode(callerName);
    }

    @GetMapping("/emptyGameSlots")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> getEmptyGameSlots() {
        String callerName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!gameService.isUserInGame()) {
            return new ResponseEntity<>(WebConstants.USER_IS_NOT_ASSIGNED_TO_GAME_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        int playersCount = gameService.getEmptyGameSlots(callerName);
        return new ResponseEntity<>(playersCount, HttpStatus.OK);
    }

    @PostMapping("/joinGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> joinGame(@Valid @RequestBody InviteCodeDto inviteCode) {
        String callerName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (gameService.isUserInGame()) {
            gameService.removePrevUserGame(callerName);
        }

        LoadGameDto loadGameDto;
        try {
            loadGameDto = gameService.addPlayerToOnlineGame(callerName, inviteCode.getInviteCode(), template);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(loadGameDto, HttpStatus.OK);
    }

    @GetMapping("/continueGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> continueGame() {
        if (!gameService.isUserInGame()) {
            return new ResponseEntity<>(WebConstants.PREV_GAME_NOT_FOUND_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        LoadGameDto loadedGame = gameService.loadPreviousPlayerGame();
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
            return new ResponseEntity<>(WebConstants.GAME_VALIDATOR_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        final char X_PAWN = 'X';
        Game game = gameService.buildGame(gameDto);
        return new ResponseEntity<>(new LoadGameDto(game, X_PAWN, X_PAWN), HttpStatus.CREATED);
    }
}
