package com.example.bff.IOS.StatisticsOfMatch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

/**
 * Тело POST /api/v1/stats/tournament/{tournamentId}/matches
 */
import com.example.bff.IOS.StatisticsOfMatch.config.LenientLocalDateTimeDeserializer;
import com.example.bff.IOS.StatisticsOfMatch.config.LocalDateTimeNoZoneSerializer;
public record TournamentStatsUpstream(

        @JsonProperty("tournamentId") UUID tournamentId,
        String sport,
        String tournamentType,               // "olympic", "round-robin" ...

        List<Match> matches                  // ↓ вложенные записи
) {

    /** Один матч внутри турнира */
    public record Match(
            @JsonProperty("matchId") UUID matchId,
            List<Participant> participants
    ) { }

    /** Статистика участника матча */
    public record Participant(
            @JsonProperty("participantId") UUID participantId,
            String  participantType,         // "player", "team", ...
            int     points,
            boolean isWinner,
            int     goals,
            int     assists,
            int     fouls,
            int     yellowCards,
            int     redCards,
            int     knockdowns,
            int     submissions,
            int     setsWon,
            int     timePlayed               // секунды / минуты — как договоритесь
    ) { }
}