package com.vatika.cardgame.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void newPlayerHasEmptyHandAndZeroTotal() {
        Player player = new Player(UUID.randomUUID(), "Vatika");
        assertEquals(0, player.totalValue());
        assertTrue(player.getHand().isEmpty());
    }

    @Test
    void receiveAddsCardsToHand() {
        Player player = new Player(UUID.randomUUID(), "Vatika");
        List<Card> cards = List.of(new Card(Suit.HEARTS, Rank.KING), new Card(Suit.SPADES, Rank.TEN));
        player.receive(cards);
        assertEquals(cards, player.getHand());
    }

    @Test
    void totalValueSumsFaceValues() {
        Player player = new Player(UUID.randomUUID(), "Vatika");
        List<Card> cards = List.of(new Card(Suit.HEARTS, Rank.KING), new Card(Suit.SPADES, Rank.TEN));
        player.receive(cards);
        assertEquals(23, player.totalValue());
    }

    @Test
    void handCannotBeModifiedFromOutside() {
        Player player = new Player(UUID.randomUUID(), "Vatika");
        List<Card> cards = List.of(new Card(Suit.HEARTS, Rank.KING), new Card(Suit.SPADES, Rank.TEN));
        player.receive(cards);
        List<Card> hand = player.getHand();
        assertThrows(
                UnsupportedOperationException.class,
                () -> hand.add(new Card(Suit.HEARTS, Rank.KING))
        );
        assertEquals(2, player.getHand().size());
    }
}
