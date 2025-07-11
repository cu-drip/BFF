package com.example.bff.IOS.StatisticOfTournament1.model;

// MatchDto.java

import java.util.List;
import java.util.UUID;

/**
 * Матч с набором участников.
 */
public record MatchDto(
        UUID matchId,
        List<ParticipantDto> participants
) { }
