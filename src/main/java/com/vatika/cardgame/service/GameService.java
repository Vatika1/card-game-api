package com.vatika.cardgame.service;

import com.vatika.cardgame.domain.*;
import com.vatika.cardgame.repository.DeckRepository;
import com.vatika.cardgame.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.random.RandomGenerator;

@Service
public class GameService {

    private final RandomGenerator randomGenerator;
    private final GameRepository gameRepository;
    private final DeckRepository deckRepository;


    public GameService(RandomGenerator randomGenerator, GameRepository gameRepository, DeckRepository deckRepository) {
        this.randomGenerator = randomGenerator;
        this.gameRepository = gameRepository;
        this.deckRepository = deckRepository;
    }

    public UUID createGame(){
        Game game = new Game(UUID.randomUUID());
        gameRepository.save(game);
        return  game.getId();
    }

    public void deleteGame(UUID gameId){
        if (!gameRepository.deleteById(gameId)) {
            throw new GameNotFoundException(gameId);
        }
    }

    public UUID createDeck(){
        Deck deck = new Deck();
        deckRepository.save(deck);
        return deck.getId();
    }
    public void addDeckToGame(UUID gameId, UUID deckId){
        Game game = findGame(gameId);
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(deckId));
        synchronized (game) {
            game.getGameDeck().addDeck(deck);
        }
    }
    public Player addPlayer(UUID gameId, String name){
        Game game = findGame(gameId);
        synchronized (game){
            return game.addPlayer(name);
        }
    }
    public void removePlayer(UUID gameId, UUID playerId){
        Game game = findGame(gameId);
        synchronized (game){
            game.removePlayer(playerId);
        }
    }

    public List<Card> dealCards(UUID gameId, UUID playerId, int count){
        Game game = findGame(gameId);
        synchronized (game){
            return game.dealCards(playerId, count);
        }
    }
    public List<Card> getPlayerCards(UUID gameId, UUID playerId){
        Game game = findGame(gameId);
        synchronized (game){
            return List.copyOf(game.getPlayer(playerId).getHand());
        }
    }
    public List<PlayerTotal> getPlayersByTotalValue(UUID gameId){
        Game game = findGame(gameId);
        synchronized (game){
            return game.playersByTotalValueDesc().stream()
                    .map(p -> new PlayerTotal(p.getId(), p.getName(), p.totalValue()))
                    .toList();
        }
    }
    public void shuffle(UUID gameId){
        Game game = findGame(gameId);
        synchronized (game){
            game.getGameDeck().shuffle(randomGenerator);
        }

    }
    public Map<Suit, Integer> getSuitCounts(UUID gameId){
        Game game = findGame(gameId);
        synchronized (game){
            return game.getGameDeck().suitCounts();
        }
    }
   public Map<Card, Integer> getCardCounts(UUID gameId){
        Game game = findGame(gameId);
        synchronized (game){
            return game.getGameDeck().cardCounts();
        }
   }

    private Game findGame(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }
}
