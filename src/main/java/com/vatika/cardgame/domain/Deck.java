package com.vatika.cardgame.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Deck {
    private final UUID id;
    private final List<Card> cards;
    private boolean used;

    public Deck() {
        this.id = UUID.randomUUID();
        List<Card> cards = new ArrayList<>(52);
        for(Suit suit: Suit.values()){
            for(Rank rank: Rank.values()){
                cards.add(new Card(suit, rank));
            }
        }
        this.cards = List.copyOf(cards);
    }

    public List<Card> takeCards(){
        if(used){
            throw new DeckAlreadyUsedException(id);
        }
        this.used = true;
        return cards;
    }

    public UUID getId() {
        return id;
    }

    public boolean isUsed() {
        return used;
    }
}
