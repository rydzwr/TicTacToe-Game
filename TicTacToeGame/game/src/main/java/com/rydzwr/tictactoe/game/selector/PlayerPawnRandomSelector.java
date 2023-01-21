package com.rydzwr.tictactoe.game.selector;

import com.rydzwr.tictactoe.game.constants.GameConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    private List<Character> buildPawnsList() {
        return GameConstants.PLAYER_PAWNS.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    }
}

