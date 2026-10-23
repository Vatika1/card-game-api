package com.vatika.cardgame.controller;

import com.vatika.cardgame.domain.Card;
import com.vatika.cardgame.domain.PlayerTotal;
import com.vatika.cardgame.dto.AddPlayerRequest;
import com.vatika.cardgame.dto.DealRequest;
import com.vatika.cardgame.dto.IdResponse;
import com.vatika.cardgame.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/games/{gameId}/players")
public class PlayerController {

    private final GameService gameService;

    public PlayerController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse addPlayer(@PathVariable UUID gameId, @Valid @RequestBody AddPlayerRequest request) {
        return new IdResponse(gameService.addPlayer(gameId, request.name()).getId());
    }

    @DeleteMapping("/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePlayer(@PathVariable UUID gameId, @PathVariable UUID playerId) {
        gameService.removePlayer(gameId, playerId);
    }

    @GetMapping
    public List<PlayerTotal> players(@PathVariable UUID gameId) {
        return gameService.getPlayersByTotalValue(gameId);
    }

    @PostMapping("/{playerId}/deal")
    public List<Card> deal(@PathVariable UUID gameId, @PathVariable UUID playerId,
                           @Valid @RequestBody DealRequest request) {
        return gameService.dealCards(gameId, playerId, request.count());
    }

    @GetMapping("/{playerId}/cards")
    public List<Card> cards(@PathVariable UUID gameId, @PathVariable UUID playerId) {
        return gameService.getPlayerCards(gameId, playerId);
    }
}
