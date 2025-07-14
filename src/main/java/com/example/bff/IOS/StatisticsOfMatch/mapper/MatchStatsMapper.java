package com.example.bff.IOS.StatisticsOfMatch.mapper;

import com.example.bff.IOS.StatisticsOfMatch.model.MatchStatisticsDto;
import com.example.bff.IOS.StatisticsOfMatch.model.TournamentStatsUpstream;   // ← пакет model
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MatchStatsMapper {

    public TournamentStatsUpstream toUpstream(UUID tournamentId,
                                              List<MatchStatisticsDto> ui) {

        List<TournamentStatsUpstream.Match> matches = ui.stream()
                .map(dto -> new TournamentStatsUpstream.Match(
                        UUID.fromString(dto.matchId()),
                        List.of(
                                toParticipant(dto.first(),  dto.goals().get(0), dto.isFirstWinner()),
                                toParticipant(dto.second(), dto.goals().get(1), !dto.isFirstWinner())
                        )
                )).toList();

        return new TournamentStatsUpstream(
                tournamentId,
                ui.get(0).sport(),
                "olympic",
                matches
        );
    }

    private TournamentStatsUpstream.Participant toParticipant(
            MatchStatisticsDto.PlayerDto p,
            int goals,
            boolean isWinner) {

        return new TournamentStatsUpstream.Participant(
                UUID.fromString(p.id()),
                "player",
                goals,          // points
                isWinner,
                goals,          // goals
                0, 0,           // assists, fouls
                0, 0,           // yellow, red
                0, 0, 0,        // knockdowns, submissions, setsWon
                p.age()         // timePlayed — заглушка
        );
    }
}
