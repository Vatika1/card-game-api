package com.vatika.cardgame.domain;

import java.util.UUID;

public record PlayerTotal(UUID playerId, String name, int totalValue) {}