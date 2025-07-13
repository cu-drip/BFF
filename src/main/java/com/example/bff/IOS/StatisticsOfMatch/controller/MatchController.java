package com.example.bff.IOS.StatisticsOfMatch.controller;

import com.example.bff.IOS.StatisticsOfMatch.client.MatchClient;
import com.example.bff.IOS.StatisticsOfMatch.model.MatchStatisticsDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/match")
public class MatchController {

    private final MatchClient matchClient;

    public MatchController(MatchClient matchClient) {
        this.matchClient = matchClient;
    }

    /**
     * UI делает GET /match/stats/{tournamentId}.
     * BFF блокирует reactive-цепочку, т.к. мы на обычном MVC-потоке.
     */
    @GetMapping("/stats/{tournamentId}")
    public List<MatchStatisticsDto> getMatchStatsByTournament(@PathVariable UUID tournamentId) {
        return matchClient
                .getMatchStatsByTournamentId(tournamentId)
                .block();
    }
}