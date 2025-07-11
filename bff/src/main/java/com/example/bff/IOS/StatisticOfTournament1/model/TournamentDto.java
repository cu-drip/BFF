package com.example.bff.IOS.StatisticOfTournament1.model;

import java.util.List;
import java.util.UUID;

/**
 * Финальный объект, который летит во фронт
 * и который возвращает Statistic-service.
 */
public record TournamentDto(
        UUID tournamentId,
        String sport,
        String tournamentType,
        List<MatchDto> matches
) { }
