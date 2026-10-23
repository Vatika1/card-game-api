package com.vatika.cardgame.domain;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeckTest {

    @Test
    void newDeckHas52UniqueCards(){
        Deck deck = new Deck();
        List<Card> cards = deck.takeCards();
        assertEquals(52, cards.size());
        assertEquals(52, new HashSet<>(cards).size());
    }

    @Test
    void takingCardsTwiceThrows() {
        Deck deck = new Deck();
        deck.takeCards();
        assertThrows(DeckAlreadyUsedException.class, deck::takeCards);
    }

}
