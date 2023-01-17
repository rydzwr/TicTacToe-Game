package com.rydzwr.tictactoe.web.controller.rest;

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
    public ResponseEntity<Object> createNewGame(@Valid @RequestBody GameDto gameDto) {
        gameBuilderService.buildGame(gameDto);
        return ResponseEntity.ok().build();
    }
}
