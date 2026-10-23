package com.vatika.cardgame.service;

import com.vatika.cardgame.domain.Card;
import com.vatika.cardgame.repository.InMemoryDeckRepository;
import com.vatika.cardgame.repository.InMemoryGameRepository;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceConcurrencyTest {

    @Test
    void concurrentDealsNeverDealSameCardTwice() throws Exception {
        GameService service = new GameService(
                new Random(42), new InMemoryGameRepository(), new InMemoryDeckRepository());

        UUID gameId = service.createGame();
        service.addDeckToGame(gameId, service.createDeck());
        service.shuffle(gameId);
        UUID playerId = service.addPlayer(gameId, "Alice").getId();

        int attempts = 100;                       // more deals than cards
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CountDownLatch start = new CountDownLatch(1);
        Queue<Card> dealt = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < attempts; i++) {
            pool.submit(() -> {
                start.await();                    // all threads start together
                dealt.addAll(service.dealCards(gameId, playerId, 1));
                return null;
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(52, dealt.size());                       // never more than one deck
        assertEquals(52, new HashSet<>(dealt).size());        // no card dealt twice
        assertEquals(52, service.getPlayerCards(gameId, playerId).size());
        assertTrue(service.getCardCounts(gameId).isEmpty());  // game deck empty
    }
}