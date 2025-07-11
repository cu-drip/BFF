package com.example.bff.IOS.StatisticOfTournament1.client;

import com.example.bff.IOS.StatisticOfTournament1.model.TournamentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Реализация на WebClient.
 * Любая ошибка → отдаём мок, чтобы фронт не упал.
 */
@Component
public class WebClientTournamentClient implements TournamentClient {

    private final WebClient webClient;

    public WebClientTournamentClient(WebClient.Builder builder,
                                     @Value("${svc.statistic.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<TournamentDto> getTournamentById(UUID id) {
        return webClient.get()
                .uri("/api/v1/tournaments/{id}", id) // путь Statistic-service
                .retrieve()
                .bodyToMono(TournamentDto.class)
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(ex -> Mono.just(mock(id)));
    }

    /** Простейший мок, чтобы фронт всегда что-то получил. */
    private TournamentDto mock(UUID id) {
        return new TournamentDto(
                id,
                "football",
                "round_robin",
                List.of()          // без матчей
        );
    }
}
