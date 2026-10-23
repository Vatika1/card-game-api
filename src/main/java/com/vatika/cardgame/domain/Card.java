package com.vatika.cardgame.domain;

import java.util.Objects;

public record Card(Suit suit, Rank rank) {

    public Card {
        Objects.requireNonNull(suit, "suit");
        Objects.requireNonNull(rank, "rank");
    }

    public int value() {
        return rank.getValue();
    }
}