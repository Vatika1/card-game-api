package com.vatika.cardgame.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    void addPlayerAddsPlayerToGame(){
        Game game = new Game(UUID.randomUUID());
        Player player = game.addPlayer("Vatika");
        assertEquals(1, game.getPlayers().size());
        assertTrue(game.getPlayers().containsKey(player.getId()));
        assertEquals("Vatika", player.getName());
    }
    @Test void removePlayerRemovesPlayer(){
        Game game = new Game(UUID.randomUUID());
        Player player = game.addPlayer("Vatika");
        game.removePlayer(player.getId());
        assertTrue(game.getPlayers().isEmpty());
    }
    @Test void removingUnknownPlayerThrows(){
        Game game = new Game(UUID.randomUUID());
        assertThrows(PlayerNotFoundException.class,
                () -> game.removePlayer(UUID.randomUUID()));
    }
    @Test void dealCardsGivesCardsToPlayerAndRemovesThemFromGameDeck(){
        Game game = new Game(UUID.randomUUID());
        game.getGameDeck().addDeck(new Deck());
        Player player = game.addPlayer("Vatika");
        List<Card> dealt = game.dealCards(player.getId(), 5);
        assertEquals(5, dealt.size());
        assertEquals(dealt, player.getHand());
        assertEquals(47, game.getGameDeck().size());
    }
    @Test void dealingToUnknownPlayerThrows(){
        Game game = new Game(UUID.randomUUID());
        game.getGameDeck().addDeck(new Deck());
        assertThrows(PlayerNotFoundException.class,
                () -> game.dealCards(UUID.randomUUID(), 5));

        assertEquals(52, game.getGameDeck().size());
    }
    @Test void playersAreSortedByTotalValueDescending(){
        Game game = new Game(UUID.randomUUID());
        Player a = game.addPlayer("A");
        Player b = game.addPlayer("B");

        b.receive(List.of(new Card(Suit.CLUBS, Rank.SEVEN), new Card(Suit.HEARTS, Rank.QUEEN)));   // 19
        a.receive(List.of(new Card(Suit.SPADES, Rank.TEN), new Card(Suit.HEARTS, Rank.KING)));     // 23

        assertEquals(List.of(a, b), game.playersByTotalValueDesc());
    }
    @Test void playersWithSameTotalAreSortedByName(){
        Game game = new Game(UUID.randomUUID());
        Player alice = game.addPlayer("A");
        Player bob = game.addPlayer("B");

        alice.receive(List.of(new Card(Suit.SPADES, Rank.TEN), new Card(Suit.HEARTS, Rank.KING)));
        bob.receive(List.of(new Card(Suit.SPADES, Rank.TEN), new Card(Suit.HEARTS, Rank.KING)));

        assertEquals(List.of(alice, bob), game.playersByTotalValueDesc());
    }
}
