package com.vatika.cardgame.dto;

import com.vatika.cardgame.domain.Rank;
import com.vatika.cardgame.domain.Suit;

public record CardCountResponse(Suit suit, Rank rank, int count) {}
