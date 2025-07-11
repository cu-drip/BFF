// src/main/java/com/example/bff/config/WebClientConfig.java
package com.example.bff.IOS.StatisticOfTournament1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient statisticWebClient(
            @Value("${svc.statistic.base-url}") String baseUrl,
            WebClient.Builder builder) {
        return builder.baseUrl(baseUrl).build();
    }
}
