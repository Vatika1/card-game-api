package com.vatika.cardgame.repository;

import com.vatika.cardgame.domain.Deck;

import java.util.Optional;
import java.util.UUID;

public interface DeckRepository {
    void save(Deck deck);
    Optional<Deck> findById(UUID id);
}
