package com.example.bff.IOS.StatisticsOfMatch.client;

import com.example.bff.IOS.StatisticsOfMatch.model.MatchStatisticsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Реализация на WebClient.
 * Любая ошибка → отдаём мок, чтобы фронт не упал.
 */
@Component
public class WebClientMatchClient implements MatchClient {

    private final WebClient webClient;

    public WebClientMatchClient(WebClient.Builder builder,
                                @Value("${svc.competition.base-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<List<MatchStatisticsDto>> getMatchStatsByTournamentId(UUID tournamentId) {
        return webClient.get()
                .uri("/MatchStats/{id}", tournamentId) // путь согласно вашему API
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<MatchStatisticsDto>>() {})
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(ex -> Mono.just(createMockData(tournamentId)));
    }

    /** Простейший мок, чтобы фронт всегда что-то получил. */
    private List<MatchStatisticsDto> createMockData(UUID tournamentId) {
        var mockPlayer1 = new MatchStatisticsDto.PlayerDto(
                "player1", "John", "Doe", "Middle", "+1234567890", "john@example.com",
                "hashedPassword", LocalDate.of(1990, 1, 1), 35, "male", 75, 180,
                LocalDateTime.now(), "Mock player 1", "avatar1.jpg", false
        );

        var mockPlayer2 = new MatchStatisticsDto.PlayerDto(
                "player2", "Jane", "Smith", "Middle", "+0987654321", "jane@example.com",
                "hashedPassword", LocalDate.of(1992, 5, 15), 33, "female", 65, 170,
                LocalDateTime.now(), "Mock player 2", "avatar2.jpg", false
        );

        var mockMatch = new MatchStatisticsDto(
                tournamentId.toString(),
                "match1",
                mockPlayer1,
                mockPlayer2,
                "football",
                90,
                List.of(2, 1),
                List.of(1, 0),
                List.of(3, 2),
                List.of(0, 0),
                List.of(0, 0),
                List.of(0, 0),
                List.of(1, 0),
                true
        );

        return List.of(mockMatch);
    }
}