package com.vatika.cardgame.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Player {

    private final UUID id;
    private final String name;
    private final List<Card> hand = new ArrayList<>();

    public Player(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    void receive(List<Card> cards) {
        hand.addAll(cards);
    }

    public int totalValue(){
        return hand.stream().mapToInt(Card::value).sum();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }
}
