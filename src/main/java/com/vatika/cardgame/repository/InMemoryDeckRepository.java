package com.vatika.cardgame.repository;

import com.vatika.cardgame.domain.Deck;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDeckRepository implements DeckRepository {

    private final Map<UUID, Deck> decks = new ConcurrentHashMap<>();
    @Override
    public void save(Deck deck) {
        decks.put(deck.getId(), deck);
    }

    @Override
    public Optional<Deck> findById(UUID id) {
        return Optional.ofNullable(decks.get(id));
    }
}
