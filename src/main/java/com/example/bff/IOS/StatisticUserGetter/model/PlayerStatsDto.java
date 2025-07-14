// src/main/java/com/example/bff/userstats/model/PlayerStatsDto.java
package com.example.bff.IOS.StatisticUserGetter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * DTO 1-в-1 c тем, что присылает Statistic-service.
 * Нужен только для десериализации ответа.
 */
public record PlayerStatsDto(
        @JsonProperty("userId")         UUID userId,
        @JsonProperty("sport")          String sport,
        @JsonProperty("totalGames")     int totalGames,
        @JsonProperty("totalPoints")    int totalPoints,
        @JsonProperty("averagePoints")  double averagePoints,
        @JsonProperty("totalWins")      int totalWins,
        @JsonProperty("totalDraws")     int totalDraws,
        @JsonProperty("totalLosses")    int totalLosses,   // <- приходящее поле
        @JsonProperty("winRate")        double winRate,
        @JsonProperty("assists")        int assists,
        @JsonProperty("fouls")          int fouls,
        @JsonProperty("yellowCards")    int yellowCards,
        @JsonProperty("redCards")       int redCards,
        @JsonProperty("setsWon")        int setsWon,
        @JsonProperty("submissions")    int submissions,
        @JsonProperty("knockdowns")     int knockdowns,
        @JsonProperty("timePlayed")     int timePlayed
) {}
