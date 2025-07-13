package com.example.bff.IOS.StatisticsOfMatch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MatchStatisticsDto(
        @JsonProperty("tournamentID") String tournamentId,
        @JsonProperty("matchID") String matchId,
        PlayerDto first,
        PlayerDto second,
        String sport,
        int timePlayed,
        List<Integer> goals,
        List<Integer> assists,
        List<Integer> fouls,
        List<Integer> points,
        List<Integer> knockdowns,
        List<Integer> setsWon,
        List<Integer> yellowCards,
        boolean isFirstWinner
) {
    public record PlayerDto(
            String id,
            String name,
            String surname,
            String patronymic,
            String phoneNumber,
            String email,
            String hashedPassword,
            LocalDate dateOfBirth,
            int age,
            String sex,
            int weight,
            int height,
            LocalDateTime createdAt,
            String bio,
            String avatarUrl,
            boolean admin
    ) {
    }
}