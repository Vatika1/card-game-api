package com.vatika.cardgame.domain;

import com.vatika.cardgame.exception.PlayerNotFoundException;

import java.util.*;

public class Game {

    private final UUID id;
    private final GameDeck gameDeck = new GameDeck();
    private final Map<UUID, Player> players = new LinkedHashMap<>();

    public Game(UUID id) {
        this.id = id;
    }

    public Player addPlayer(String name){
        Player player = new Player(UUID.randomUUID(), name);
        players.put(player.getId(), player);
        return player;
    }

    public void removePlayer(UUID playerId){
        if(!players.containsKey(playerId)){
            throw new PlayerNotFoundException(playerId);
        }
        players.remove(playerId);
    }

    public Player getPlayer(UUID playerId){
        Player player = players.get(playerId);
        if(player == null){
            throw new PlayerNotFoundException(playerId);
        }
        return player;
    }

    public List<Card> dealCards(UUID playerId, int count){
        Player player = getPlayer(playerId);
        List<Card> dealt = gameDeck.dealCards(count);
        player.receive(dealt);
        return dealt;
    }

    public List<Player> playersByTotalValueDesc(){
        return players.values().stream()
                .sorted(Comparator.comparingInt(Player::totalValue).reversed()
                        .thenComparing(Player::getName))
                .toList();
    }
    public UUID getId() {
        return id;
    }

    public GameDeck getGameDeck() {
        return gameDeck;
    }

    public Map<UUID, Player> getPlayers() {
        return Collections.unmodifiableMap(players);
    }
}
