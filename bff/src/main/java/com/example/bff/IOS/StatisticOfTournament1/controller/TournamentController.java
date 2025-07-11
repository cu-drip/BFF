// src/main/java/com/example/bff/controller/TournamentController.java
package com.example.bff.IOS.StatisticOfTournament1.controller;

// TournamentController.java

import com.example.bff.IOS.StatisticOfTournament1.client.TournamentClient;
import com.example.bff.IOS.StatisticOfTournament1.model.TournamentDto;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tournament")     // финальная точка для UI
public class TournamentController {

    private final TournamentClient tournamentClient;

    public TournamentController(TournamentClient tournamentClient) {
        this.tournamentClient = tournamentClient;
    }

    /**
     * UI делает GET /tournament/{id}.
     * BFF блокирует reactive-цепочку, т.к. мы на обычном MVC-потоке.
     */
    @GetMapping("/{tournamentId}")
    public TournamentDto getById(@PathVariable UUID tournamentId) {
        return tournamentClient
                .getTournamentById(tournamentId)
                .block();          // ok: однократный вызов во внешнюю систему
    }
}
