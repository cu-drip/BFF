// src/main/java/com/example/bff/userstats/model/UserStatDto.java
package com.example.bff.IOS.StatisticUserGetter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO, который BFF отдает UI.
 */
public record UserStatDto(
        String sport,
        int totalWins,

        // UI попросил «totalLoses» (sic!), поэтому меняем имя свойства
        @JsonProperty("totalLoses") int totalLoses,

        // UI попросил «winrate» в snake-case
        @JsonProperty("winrate") double winrate
) {}
