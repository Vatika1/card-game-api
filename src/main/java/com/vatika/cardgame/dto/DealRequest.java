package com.vatika.cardgame.dto;

import jakarta.validation.constraints.Positive;

public record DealRequest(@Positive int count) {}
