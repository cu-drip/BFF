// src/main/java/com/example/bff/IOS/StatisticsOfMatch/config/LenientLocalDateTimeDeserializer.java
package com.example.bff.IOS.StatisticsOfMatch.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LenientLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String str = p.getText();
        // если приходит с Z на конце — отрежем
        if (str.endsWith("Z")) {
            str = str.substring(0, str.length() - 1);
        }
        return LocalDateTime.parse(str, FORMAT);
    }
}
