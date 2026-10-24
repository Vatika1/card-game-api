package com.vatika.cardgame.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID gameId) {
        super("Game not found: " + gameId);
    }
}