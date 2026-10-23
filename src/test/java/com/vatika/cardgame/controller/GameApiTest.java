package com.vatika.cardgame.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GameApiTest {

    @Autowired
    private MockMvc mockMvc;

    private String createAndGetId(String url) throws Exception {
        String json = mockMvc.perform(post(url))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.id");
    }

    private String addPlayer(String gameId, String name) throws Exception {
        String json = mockMvc.perform(post("/api/games/{g}/players", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$.id");
    }

    private void addDeck(String gameId, String deckId) throws Exception {
        mockMvc.perform(post("/api/games/{g}/game-deck/decks", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deckId\":\"" + deckId + "\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void fullFlowDealsCardsAndUpdatesCounts() throws Exception {
        String gameId = createAndGetId("/api/games");
        addDeck(gameId, createAndGetId("/api/decks"));
        String playerId = addPlayer(gameId, "Alice");

        mockMvc.perform(post("/api/games/{g}/game-deck/shuffle", gameId))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/games/{g}/players/{p}/deal", gameId, playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"count\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        mockMvc.perform(get("/api/games/{g}/players/{p}/cards", gameId, playerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        mockMvc.perform(get("/api/games/{g}/game-deck/card-counts", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(47)));
    }

    @Test
    void addingSameDeckTwiceReturns409() throws Exception {
        String gameId = createAndGetId("/api/games");
        String deckId = createAndGetId("/api/decks");
        addDeck(gameId, deckId);

        mockMvc.perform(post("/api/games/{g}/game-deck/decks", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deckId\":\"" + deckId + "\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"));
    }

    @Test
    void dealWithZeroCountReturns400() throws Exception {
        String gameId = createAndGetId("/api/games");
        String playerId = addPlayer(gameId, "Alice");

        mockMvc.perform(post("/api/games/{g}/players/{p}/deal", gameId, playerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"count\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankPlayerNameReturns400() throws Exception {
        String gameId = createAndGetId("/api/games");

        mockMvc.perform(post("/api/games/{g}/players", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownGameReturns404() throws Exception {
        mockMvc.perform(get("/api/games/{g}/players", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletedGameReturns404() throws Exception {
        String gameId = createAndGetId("/api/games");

        mockMvc.perform(delete("/api/games/{g}", gameId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/games/{g}/players", gameId))
                .andExpect(status().isNotFound());
    }
}