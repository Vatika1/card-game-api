package com.vatika.cardgame.controller;

import com.vatika.cardgame.dto.IdResponse;
import com.vatika.cardgame.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decks")
public class DeckController {

    private final GameService gameService;

    public DeckController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse createDeck() {
        return new IdResponse(gameService.createDeck());
    }
}
