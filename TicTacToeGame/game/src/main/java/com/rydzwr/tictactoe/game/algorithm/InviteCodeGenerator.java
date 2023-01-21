package com.rydzwr.tictactoe.game.algorithm;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class InviteCodeGenerator {
    public String generateCode() {
        Random rand = new Random();
        int code = rand.nextInt(9000) + 1000;
        return Integer.toString(code);
    }
}
