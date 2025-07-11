package com.example.bff.IOS.StatisticOfTournament1.client;

import com.example.bff.IOS.StatisticOfTournament1.model.TournamentDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * BFF общается с Statistic-service только этим методом.
 */
public interface TournamentClient {
    Mono<TournamentDto> getTournamentById(UUID id);
}