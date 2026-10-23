package com.vatika.cardgame.domain;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID gameId) {
        super("Game not found: " + gameId);
    }
}