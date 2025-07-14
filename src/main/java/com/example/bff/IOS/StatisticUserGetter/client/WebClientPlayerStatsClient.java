package com.example.bff.IOS.StatisticUserGetter.client;

import com.example.bff.IOS.StatisticUserGetter.model.PlayerStatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class WebClientPlayerStatsClient implements PlayerStatsClient {

    private final WebClient statisticWebClient;

    // единственный конструктор; Spring подставит бин "statisticWebClient"
    public WebClientPlayerStatsClient(
            @Qualifier("statisticWebClient") WebClient statisticWebClient) {
        this.statisticWebClient = statisticWebClient;
    }

    @Override
    public Mono<List<PlayerStatsDto>> getStatsByUserId(UUID userId) {
        return statisticWebClient.get()
                .uri("/api/v1/stats/player/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PlayerStatsDto>>() {})
                .timeout(Duration.ofSeconds(2))
                .doOnError(e -> log.warn("Statistic-service error: {}", e.getMessage()))
                .onErrorResume(e -> Mono.just(List.of()));
    }
}
