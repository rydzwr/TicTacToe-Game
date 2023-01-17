package com.rydzwr.tictactoe.web.controller;

import com.rydzwr.tictactoe.database.dto.GameDto;
import com.rydzwr.tictactoe.database.model.User;
import com.rydzwr.tictactoe.database.repository.PlayerRepository;
import com.rydzwr.tictactoe.database.repository.UserRepository;
import com.rydzwr.tictactoe.game.service.GameBuilderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/game")
public class GameController {
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final GameBuilderService gameBuilderService;

    @GetMapping("/canResumeGame")
    @PreAuthorize("hasAuthority('USER')")
    public boolean isUserInGame(HttpServletRequest request) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User caller = userRepository.findByName(userName);
        return playerRepository.existsByUser(caller);
    }

    @PostMapping("/createGame")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Object> createNewGame(@Valid @RequestBody GameDto gameDto, HttpServletRequest request) {
        gameBuilderService.buildGame(gameDto);
        return ResponseEntity.ok().build();

        // TODO NEED TO USE STRATEGY DESIGN PATTER NO CHOOSE PROPER BUILDER USING "GAME TYPE" VALUE
        // SINGLE PLAYER GAME TYPE
        // IN ONE REQUEST BACKEND NEED TO GET:
        // 1. GAME SIZE
        // 2. GAME DIFFICULTY
        // 3. GAME OPPONENTS COUNT
        // 4. EACH PLAYERS PAWN

        // GAME BUILDER IS CREATING GAME BOARD
        // USING GAME OPPONENTS COUNT, PLAYER FACTORY NEEDS TO CREATE ALL PLAYERS INSTANCES
        // SETTING UP THEIR PAWNS

        // THEN GAME BUILDER ADDS ALL PLAYERS TO GAME

        // THEN WEBSOCKET CAN BE CREATED

        /*
        try {
            GameStrategySelector factory = new GameStrategySelector();
            BuildGameStrategy strategy = factory.chooseStrategy(gameDto.getGameType());
            Game game = strategy.buildGame(gameDto);
            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
         */

        //---------------------------------------------------------------------------------------------

        /*
        Game game = new GameBuilder(gameDto.getGameSize(), gameDto.getGameDifficulty()).build();
        gameService.save(game);
        String userName = jwtService.getAuthFromToken(request).getName();
        log.info(userName);
        User user = userRepository.findByName(userName);
        PlayerBuilder playerBuilder = new PlayerBuilder(user);
        Player player = playerBuilder.setPawn('X').setGame(game).build();
        playerService.save(player);
        gameService.save(game);
        return ResponseEntity.status(HttpStatus.CREATED).build();
         */
    }
}
