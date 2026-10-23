package com.vatika.cardgame.dto;

import jakarta.validation.constraints.NotBlank;

public record AddPlayerRequest(@NotBlank String name) {}
