package com.vatika.cardgame.domain;

import com.vatika.cardgame.exception.DeckAlreadyUsedException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameDeckTest {

    @Test
    void dealingOneDeckOneByOneReturnsAll52UniqueCardsThenNothing() {
        GameDeck gameDeck = new GameDeck();
        gameDeck.addDeck(new Deck());
        gameDeck.shuffle(new Random(42));
        List<Card> dealt = new ArrayList<>();

        for(int i = 0; i < 52; i++){
            dealt.addAll(gameDeck.dealCards(1));
        }
        List<Card> result = gameDeck.dealCards(1);
        assertEquals(52, dealt.size());
        assertEquals(52, new HashSet<>(dealt).size());
        assertTrue(result.isEmpty());
    }

    @Test
    void dealingTwoDecksReturnsEachCardExactlyTwiceThenNothing() {
        GameDeck gameDeck = new GameDeck();
        gameDeck.addDeck(new Deck());
        gameDeck.addDeck(new Deck());
        gameDeck.shuffle(new Random(42));
        List<Card> dealt = new ArrayList<>();

        for(int i = 0; i < 104; i++){
            dealt.addAll(gameDeck.dealCards(1));
        }
        List<Card> result = gameDeck.dealCards(1);
        Map<Card, Integer> counts = new HashMap<>();
        for(Card card: dealt){
            counts.merge(card, 1, Integer::sum);
        }
        assertEquals(104, dealt.size());
        assertTrue(counts.values().stream()
                .allMatch(value -> value == 2));
        assertTrue(result.isEmpty());
    }

    @Test
    void dealingMoreThanRemainingReturnsOnlyWhatIsLeft() {
        GameDeck gameDeck = new GameDeck();
        gameDeck.addDeck(new Deck());
        gameDeck.shuffle(new Random(42));

        List<Card> dealt = gameDeck.dealCards(60);
        assertEquals(52, dealt.size());
        assertEquals(52, new HashSet<>(dealt).size());
        assertEquals(0, gameDeck.size());
    }

    @Test
    void dealingZeroOrNegativeCountThrows() {
        GameDeck gameDeck = new GameDeck();
        gameDeck.addDeck(new Deck());
        gameDeck.shuffle(new Random(42));

        assertThrows(IllegalArgumentException.class, () -> gameDeck.dealCards(0));
        assertThrows(IllegalArgumentException.class, () -> gameDeck.dealCards(-1));
    }

    @Test
    void shuffleWithSameSeedProducesSameOrder() {
        GameDeck gameDeck1 = new GameDeck();
        gameDeck1.addDeck(new Deck());
        gameDeck1.shuffle(new Random(42));
        List<Card> dealt1 = gameDeck1.dealCards(52);

        GameDeck gameDeck2 = new GameDeck();
        gameDeck2.addDeck(new Deck());
        gameDeck2.shuffle(new Random(42));
        List<Card> dealt2 = gameDeck2.dealCards(52);

        assertEquals(dealt1, dealt2);
    }

    @Test
    void shuffleChangesCardOrder() {
        GameDeck gameDeck1 = new GameDeck();
        gameDeck1.addDeck(new Deck());
        gameDeck1.shuffle(new Random(42));
        List<Card> shuffled = gameDeck1.dealCards(52);

        GameDeck gameDeck2 = new GameDeck();
        gameDeck2.addDeck(new Deck());
        List<Card> unshuffled = gameDeck2.dealCards(52);

        assertNotEquals(unshuffled, shuffled);
    }

    @Test
    void suitCountsReflectUndealtCards() {
        GameDeck gameDeck = new GameDeck();
        gameDeck.addDeck(new Deck());
        gameDeck.dealCards(3);
        Map<Suit, Integer> suits = gameDeck.suitCounts();

        assertEquals(Map.of(Suit.HEARTS, 13, Suit.SPADES, 13,
                Suit.CLUBS, 13, Suit.DIAMONDS, 10), suits);
    }

    @Test
    void cardCountsAreSortedBySuitThenRankHighToLow() {
        GameDeck gameDeck = new GameDeck();
        gameDeck.addDeck(new Deck());
        Map<Card, Integer> cardCounts = gameDeck.cardCounts();

        List<Card> actual = new ArrayList<>(cardCounts.keySet());

        List<Card> expected = new ArrayList<>();
        Rank[] ranks = Rank.values();
        for (Suit suit : Suit.values()) {
            for (int i = ranks.length - 1; i >= 0; i--) {
                expected.add(new Card(suit, ranks[i]));
            }
        }
        assertEquals(expected, actual);
    }

    @Test
    void addingSameDeckTwiceThrows() {
        GameDeck gameDeck = new GameDeck();
        Deck deck = new Deck();
        gameDeck.addDeck(deck);
        assertThrows(DeckAlreadyUsedException.class, () -> gameDeck.addDeck(deck));
    }
}
