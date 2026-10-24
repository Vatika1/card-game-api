package com.vatika.cardgame.exception;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(UUID playerId) {
        super("Player not found: " + playerId);
    }
}