package com.vatika.cardgame.controller;

import com.vatika.cardgame.domain.Suit;
import com.vatika.cardgame.dto.AddDeckRequest;
import com.vatika.cardgame.dto.CardCountResponse;
import com.vatika.cardgame.dto.IdResponse;
import com.vatika.cardgame.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse createGame() {
        return new IdResponse(gameService.createGame());
    }

    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable UUID gameId) {
        gameService.deleteGame(gameId);
    }

    @PostMapping("/{gameId}/game-deck/decks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addDeck(@PathVariable UUID gameId, @Valid @RequestBody AddDeckRequest request) {
        gameService.addDeckToGame(gameId, request.deckId());
    }

    @PostMapping("/{gameId}/game-deck/shuffle")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void shuffle(@PathVariable UUID gameId) {
        gameService.shuffle(gameId);
    }

    @GetMapping("/{gameId}/game-deck/suit-counts")
    public Map<Suit, Integer> suitCounts(@PathVariable UUID gameId) {
        return gameService.getSuitCounts(gameId);
    }

    @GetMapping("/{gameId}/game-deck/card-counts")
    public List<CardCountResponse> cardCounts(@PathVariable UUID gameId) {
        return gameService.getCardCounts(gameId).entrySet().stream()
                .map(e -> new CardCountResponse(e.getKey().suit(), e.getKey().rank(), e.getValue()))
                .toList();
    }
}
