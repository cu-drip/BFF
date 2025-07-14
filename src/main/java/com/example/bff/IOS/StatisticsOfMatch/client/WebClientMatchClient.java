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
    import org.springframework.beans.factory.annotation.Qualifier;


    import com.example.bff.IOS.StatisticsOfMatch.mapper.MatchStatsMapper;
    import com.example.bff.IOS.StatisticsOfMatch.model.MatchStatisticsDto;
    import com.example.bff.IOS.StatisticsOfMatch.model.TournamentStatsUpstream;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.stereotype.Component;
    import org.springframework.web.reactive.function.client.WebClient;
    import reactor.core.publisher.Mono;


    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    /**
     * Реализация на WebClient.
     * Любая ошибка → отдаём мок, чтобы фронт не упал.
     */
    @Component
    public class WebClientMatchClient implements MatchClient {
        private static final Logger log = LoggerFactory.getLogger(WebClientMatchClient.class);

        private final WebClient webClient;
        private final MatchStatsMapper mapper;

        public WebClientMatchClient(@Qualifier("statisticWebClient") WebClient webClient, MatchStatsMapper mapper) {
            this.webClient = webClient;
            this.mapper    = mapper;
        }

        @Override
        public Mono<List<MatchStatisticsDto>> getMatchStatsByTournamentId(UUID id) {
            return webClient.get()
                    .uri("/api/v1/MatchStats/{id}", id)                       // ① без 'uri:'
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<MatchStatisticsDto>>() {})  // ② явно List<...>
                    .timeout(Duration.ofSeconds(2))
                    .onErrorResume(ex -> Mono.just(createMockData(id)));      // ③ типы теперь совпадают
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



        @Override
        public Mono<Void> saveMatchStats(UUID tournamentId, List<MatchStatisticsDto> stats) {
            // собираем тело
            TournamentStatsUpstream body = mapper.toUpstream(tournamentId, stats);

            return webClient.post()
                    .uri("/api/v1/stats/tournament/{id}/matches", tournamentId)
                    .bodyValue(body)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            // upstream отдал 200–299: завершаем цепочку успешно
                            return Mono.empty();
                        }
                        // любой другой статус — достаём текст ошибки и превращаем в исключение
                        return response.bodyToMono(String.class)
                                .flatMap(errBody -> Mono.error(new IllegalStateException(
                                        "Upstream error: " +
                                                response.statusCode() + " -> " + errBody
                                )));
                    })
                    .timeout(Duration.ofSeconds(2))
                    .onErrorResume(ex -> {
                        // сюда попадут и WebClientResponseException (404,405…), и таймауты, и наше IllegalStateException
                        // залогируем и «поглотим», чтобы фронт не увидел 500
                        log.warn("Не удалось сохранить статистику матчей: {}", ex.getMessage());
                        return Mono.empty();
                    }).then();
        }
    }