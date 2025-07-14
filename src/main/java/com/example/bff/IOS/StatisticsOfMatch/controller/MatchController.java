package com.example.bff.IOS.StatisticsOfMatch.controller;

import com.example.bff.IOS.StatisticsOfMatch.client.MatchClient;
import com.example.bff.IOS.StatisticsOfMatch.model.MatchStatisticsDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/v1spec/MatchStats")
public class MatchController {

    /**
     * UI делает GET /match/stats/{tournamentId}.
     * BFF блокирует reactive-цепочку, т.к. мы на обычном MVC-потоке.
     */

    private final MatchClient matchClient;
    public MatchController(MatchClient matchClient) {
        this.matchClient = matchClient;
    }

    @GetMapping("/{tournamentId}")             // UI зовёт /api/v1/MatchStats/{id}
    public List<MatchStatisticsDto> get(@PathVariable UUID tournamentId) {
        return matchClient.getMatchStatsByTournamentId(tournamentId)
                .block();            // оставляем блокировку, как было
    }

    @PostMapping("/{tournamentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@PathVariable UUID tournamentId,
                     @RequestBody List<MatchStatisticsDto> stats) {

        matchClient.saveMatchStats(tournamentId, stats).block();
    }
}