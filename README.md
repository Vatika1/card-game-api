# Card Game API

REST API for a basic multi-player card game: create games and 52-card decks, add decks to a game deck, add/remove players, shuffle, deal cards, and query hands and remaining cards.

Java 21, Spring Boot 4, Maven.

## How to run

**With Maven**
```
./mvnw spring-boot:run        # Windows: .\mvnw spring-boot:run
```

**With Docker**
```
docker build -t card-game-api .
docker run -p 8080:8080 card-game-api
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

**Tests**
```
./mvnw test
```

## API

| Method | Path | Description |
|---|---|---|
| POST | `/api/games` | Create a game |
| DELETE | `/api/games/{gameId}` | Delete a game |
| POST | `/api/decks` | Create a 52-card deck |
| POST | `/api/games/{gameId}/game-deck/decks` | Add a deck to the game deck `{"deckId": "..."}` |
| POST | `/api/games/{gameId}/game-deck/shuffle` | Shuffle the game deck |
| GET | `/api/games/{gameId}/game-deck/suit-counts` | Undealt cards per suit |
| GET | `/api/games/{gameId}/game-deck/card-counts` | Undealt count per card, sorted by suit then King → Ace |
| POST | `/api/games/{gameId}/players` | Add a player `{"name": "..."}` |
| DELETE | `/api/games/{gameId}/players/{playerId}` | Remove a player |
| GET | `/api/games/{gameId}/players` | Players with total hand value, highest first |
| POST | `/api/games/{gameId}/players/{playerId}/deal` | Deal cards `{"count": n}` |
| GET | `/api/games/{gameId}/players/{playerId}/cards` | A player's cards |

Errors use the RFC 9457 `ProblemDetail` format: 400 invalid input, 404 unknown game/deck/player, 409 deck already used.

## Design decisions

- **Layered structure:** `controller` → `service` → `repository`, with the game rules in `domain`. Controllers only map HTTP to service calls; no business logic lives there.
- **Domain model:** `Card` is an immutable record (`Suit` + `Rank`). `Deck` builds its 52 cards in its constructor. `GameDeck` is the pile of undealt cards (the "shoe"); decks are poured into it. `Game` holds one `GameDeck` and its players.
- **Dealing is split:** `GameDeck.dealCards` only removes cards from the pile; `Game.dealCards` connects the pile to a player's hand. Each class has one job.
- **Shuffle:** hand-written Fisher–Yates (`nextInt(i + 1)`), no library shuffle. The `RandomGenerator` is injected (`SecureRandom` in the app, seeded `Random` in tests), so shuffles are testable.
- **Sorting:** suit order comes from the `Suit` enum declaration order (hearts, spades, clubs, diamonds). Card counts use a `TreeMap` with a comparator; players are sorted by total value, then name.
- **Concurrency:** Spring serves each request on its own thread. Repositories use `ConcurrentHashMap`; every game operation runs inside `synchronized (game)`, so two requests on the same game can't deal the same card twice, while different games never block each other. The service returns snapshots (copied lists, `PlayerTotal` records), never live mutable objects, so no state escapes the lock. `Deck.takeCards()` is also `synchronized`, because the per-game lock doesn't cover the same deck being added to two different games at the same moment.
- **Repositories behind interfaces:** in-memory today; a database implementation could replace them without changing the service.
- **Errors:** one `@RestControllerAdvice` maps domain exceptions to status codes using Spring's built-in `ProblemDetail` (RFC 9457). `spring.mvc.problemdetails.enabled` makes Spring's own errors (malformed JSON, invalid UUIDs) use the same format, and the handler is ordered first so custom messages like "count must be greater than 0" aren't replaced by Spring's generic ones.

## Assumptions

- Face values: Ace = 1, 2–10 = face value, Jack = 11, Queen = 12, King = 13.
- Dealing from an empty game deck returns an empty list (200), not an error.
- Asking for more cards than remain deals whatever is left.
- A deal count must be at least 1; otherwise 400.
- A deck can be added to only one game deck, once; reusing it returns 409. Decks can't be removed once added.
- A removed player's cards are discarded, not returned to the game deck.
- Players are identified by a server-generated UUID; names don't have to be unique.
- Ties in total value are sorted alphabetically by name.
- Deleting a game deletes its players and game deck.

## Production improvements

- Persist games in a database (repository interfaces already allow this); replace the per-game `synchronized` lock with optimistic locking or row-level locks so it works across multiple instances.
- Authentication/authorization (which player may act on which game).
- Add `@WebMvcTest` slices per controller and Testcontainers integration tests once a real database exists.
- `Location` headers on 201 responses, pagination for large games, API versioning.
- Metrics and structured logging; game cleanup/expiry for abandoned games.

## Testing

- **Unit tests** for `Deck`, `GameDeck`, `Player`, `Game`, including the spec's 52 deals then a 53rd that returns nothing (and the same with 2 decks), a seeded-shuffle test, sorting, and counts.
- **Integration tests** (`@SpringBootTest` + MockMvc) run real HTTP requests through controllers, service and repositories, covering the main flow and 400/404/409 errors.
- **Concurrency test:** 8 threads fire 100 simultaneous deals at one game; exactly 52 unique cards come out, proving the per-game lock works.

## Use of AI

I used Claude (Anthropic) throughout:
- **Understanding the domain:** I had no card-game background, so I used it to clarify terms (suit, rank, shoe/game deck) and the spec's edge cases.
- **Design:** I designed the domain model (which classes exist and what each owns) and the REST endpoints from the spec, and used AI to challenge it. For example, it pointed out that a separate `Shoe` class duplicated `GameDeck`.
- **Code generation:** AI generated much of the code from my design, mostly boilerplate (DTOs, controllers, config, repositories, exception handler), plus first drafts of domain classes. I reviewed every line and changed what I disagreed with, for example:
  - replaced a static factory `Deck.standard()` with a plain constructor, since only one kind of deck exists;
  - removed an unused `getCards()` getter that would have let callers bypass the "deck used once" rule;
  - removed a redundant `TreeMap` → `LinkedHashMap` copy;
  - asked for response objects instead of returning domain objects, which led to the `PlayerTotal` snapshot that fixed a real concurrency gap;
  - rejected non-standard package names and used the conventional Spring layout (`controller`, `service`, `repository`, `dto`, `exception`, `config`);
  - insisted on a concurrency test for simultaneous deals, which the AI had suggested as optional.
- **Tests:** I wrote the domain unit tests myself, and AI reviewed them for gaps. AI drafted the MockMvc integration tests and the concurrency test; I went through each one step by step, ran them, and can explain every assertion.
- **Post-submission review:** an AI-assisted review of the final repo flagged a race: two different games could claim the same deck at once. I confirmed it with a stress test (it reproduced 2 times in 200,000 runs), fixed it by synchronizing `Deck.takeCards()`, and verified 0 failures afterwards. Manual end-to-end testing of the fix also caught a regression where enabling `ProblemDetail` replaced my validation messages; I fixed the handler order and added a test assertion so it can't come back unnoticed.