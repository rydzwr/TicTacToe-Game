package com.rydzwr.tictactoe.web.controller.rest;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.service.dto.incoming.GameDto;
import com.rydzwr.tictactoe.service.dto.incoming.InviteCodeDto;
import com.rydzwr.tictactoe.service.dto.outgoing.LoadGameDto;
import com.rydzwr.tictactoe.service.game.GameService;
import com.rydzwr.tictactoe.service.game.constants.GameConstants;
import com.rydzwr.tictactoe.service.game.validator.GameDtoValidator;
import com.rydzwr.tictactoe.web.constants.WebConstants;
import com.rydzwr.tictactoe.web.service.WebSocketService;
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
    private final WebSocketService webSocketService;

    @GetMapping("/canResumeGame")
    @PreAuthorize("hasAuthority('USER')")
    public boolean isUserInGame() {
        var userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return gameService.isUserInGame(userName);
    }

    @GetMapping("/inviteCode")
    @PreAuthorize("hasAuthority('USER')")
    public String getInviteCode() {
        String callerName = SecurityContextHolder.getContext().getAuthentication().getName();
        return gameService.getInviteCode(callerName);
    }

    @PostMapping("/joinGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> joinGame(@Valid @RequestBody InviteCodeDto inviteCode) {
        String callerName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (gameService.isUserInGame(callerName)) {
            gameService.removePrevUserGame(callerName);
        }

        try {
            var loadGameDto = gameService.addPlayerToOnlineGame(callerName, inviteCode.getInviteCode());
            webSocketService.updateAwaitingPlayersLobby(loadGameDto.getAwaitingPlayers());

            if (loadGameDto.getAwaitingPlayers() == 0) {
                webSocketService.startOnlineGame();
            }

            return new ResponseEntity<>(loadGameDto, HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/continueGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> continueGame() {
        var userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!gameService.isUserInGame(userName)) {
            return new ResponseEntity<>(WebConstants.PREV_GAME_NOT_FOUND_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        LoadGameDto loadedGame = gameService.loadPreviousPlayerGame(userName);
        return new ResponseEntity<>(loadedGame, HttpStatus.OK);
    }

    @PostMapping("/createGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> createNewGame(@Valid @RequestBody GameDto gameDto) {
        var callerName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!validator.validateReceivedData(gameDto)) {
            return new ResponseEntity<>(WebConstants.GAME_VALIDATOR_EXCEPTION, HttpStatus.BAD_REQUEST);
        }

        if (gameService.isUserInGame(callerName)) {
            gameService.removePrevUserGame(callerName);
        }

        var playersToJoin = gameDto.getHumanGameSlots() - 1;
        Game game = gameService.buildGame(gameDto, callerName);
        var loadGameDto = new LoadGameDto(game, GameConstants.DEFAULT_STARTING_PAWN, GameConstants.DEFAULT_STARTING_PAWN, playersToJoin);
        return new ResponseEntity<>(loadGameDto, HttpStatus.CREATED);
    }
}
