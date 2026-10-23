package com.vatika.cardgame.repository;

import com.vatika.cardgame.domain.Game;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository{
    private final Map<UUID, Game> games = new ConcurrentHashMap<>();
    @Override
    public void save(Game game) {
        games.put(game.getId(), game);
    }

    @Override
    public Optional<Game> findById(UUID id) {
        return Optional.ofNullable(games.get(id));
    }

    @Override
    public boolean deleteById(UUID id) {
        return games.remove(id) != null;
    }
}
