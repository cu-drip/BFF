// src/main/java/com/example/bff/model/UiTournamentDto.java
package com.example.bff.IOS.StatisticOfTournament1.model;

import java.util.UUID;

public record UiTournamentDto(
        UUID tournamentId,
        String sport,
        String tournamentType
) {}
