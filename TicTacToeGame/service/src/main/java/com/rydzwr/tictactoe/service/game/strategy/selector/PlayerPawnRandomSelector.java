package com.rydzwr.tictactoe.service.game.strategy.selector;

import com.rydzwr.tictactoe.database.model.Game;
import com.rydzwr.tictactoe.database.model.Player;
import com.rydzwr.tictactoe.game.constants.GameConstants;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class PlayerPawnRandomSelector {
    List<Character> characters;

    public PlayerPawnRandomSelector() {
        characters = buildPawnsList();
    }

    public char selectPawn() {
        Random rand = new Random();
        int index = rand.nextInt(characters.size());
        return characters.remove(index);
    }

    public char selectPawn(List<Character> characters) {
        Random rand = new Random();
        int index = rand.nextInt(characters.size());
        return characters.remove(index);
    }

    private List<Character> buildPawnsList() {
        return GameConstants.PLAYER_PAWNS.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    }

    public char selectAvailablePawn(Game game) {
        List<Character> allPawns = buildPawnsList();
        List<Character> occupiedPawns = game.getPlayers().stream().map(Player::getPawn).toList();
        allPawns.removeAll(occupiedPawns);
        return selectPawn(allPawns);
    }
}

