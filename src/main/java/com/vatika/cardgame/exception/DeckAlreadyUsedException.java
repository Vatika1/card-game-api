package com.vatika.cardgame.exception;

import java.util.UUID;

public class DeckAlreadyUsedException extends RuntimeException {
    public DeckAlreadyUsedException(UUID deckId) {
        super("Deck already added to a game deck: " + deckId);
    }
}