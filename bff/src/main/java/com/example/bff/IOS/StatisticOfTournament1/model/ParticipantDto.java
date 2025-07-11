package com.example.bff.IOS.StatisticOfTournament1.model;

import java.util.UUID;

/**
 * Один участник конкретного матча.
 * Поля ровно такие, какие ждёт фронт.
 */
public record ParticipantDto(
        UUID participantId,
        String participantType,
        int points,
        boolean isWinner,
        int goals,
        int assists,
        int fouls,
        int yellowCards,
        int redCards,
        int knockdowns,
        int submissions,
        int setsWon,
        int timePlayed
) { }