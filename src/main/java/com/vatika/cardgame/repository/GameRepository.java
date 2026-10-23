package com.vatika.cardgame.repository;

import com.vatika.cardgame.domain.Game;

import java.util.Optional;
import java.util.UUID;

public interface GameRepository{
    void save(Game game);
    Optional<Game> findById(UUID id);
    boolean deleteById(UUID id);
}
