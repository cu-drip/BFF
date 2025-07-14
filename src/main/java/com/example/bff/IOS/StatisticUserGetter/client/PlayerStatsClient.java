// src/main/java/com/example/bff/userstats/client/PlayerStatsClient.java
package com.example.bff.IOS.StatisticUserGetter.client;

import com.example.bff.IOS.StatisticUserGetter.model.PlayerStatsDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/** BFF ↔ Statistic-service. */
public interface PlayerStatsClient {
    Mono<List<PlayerStatsDto>> getStatsByUserId(UUID userId);
}
