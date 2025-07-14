// src/main/java/com/example/bff/userstats/service/UserStatService.java
package com.example.bff.IOS.StatisticUserGetter.service;

import com.example.bff.IOS.StatisticUserGetter.client.PlayerStatsClient;
import com.example.bff.IOS.StatisticUserGetter.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserStatService {

    private final PlayerStatsClient client;

    public Mono<List<UserStatDto>> getUserStats(UUID userId) {
        return client.getStatsByUserId(userId)
                .map(this::mapToUiDto);
    }

    // ---- helpers ----
    private List<UserStatDto> mapToUiDto(List<PlayerStatsDto> stats) {
        return stats.stream()
                .map(ps -> new UserStatDto(
                        ps.sport(),
                        ps.totalWins(),
                        ps.totalLosses(),  // меняем только имя поля
                        ps.winRate()
                ))
                .toList();
    }
}
