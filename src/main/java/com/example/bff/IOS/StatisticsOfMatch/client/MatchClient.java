package com.example.bff.IOS.StatisticsOfMatch.client;

import com.example.bff.IOS.StatisticsOfMatch.model.MatchStatisticsDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * BFF общается с Statistic-service для получения статистики матчей.
 */
public interface MatchClient {
    Mono<List<MatchStatisticsDto>> getMatchStatsByTournamentId(UUID tournamentId);
}