package com.example.bff.WEB.ProxyRequests.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1web")
public class GenericProxyController {

    private final Map<String, WebClient> clients;

    public GenericProxyController(
            @Qualifier("userWebClient") WebClient userClient,
            @Qualifier("competitionWebClient") WebClient competitionClient,
            @Qualifier("feedbackWebClient") WebClient feedbackClient,
            @Qualifier("chatWebClient") WebClient chatClient
    ) {
        this.clients = Map.of(
                "user", userClient,
                "competition", competitionClient,
                "feedback", feedbackClient,
                "chat", chatClient,
                "tournaments", competitionClient
        );
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest req) {

        // 1. снимаем только prefix "/api/v1web"
        String pathAfterPrefix = req.getRequestURI().replaceFirst("/api/v1web", ""); // "/tournaments" или "/competition/stats/42"

        // 2. первый сегмент = алиас → какой WebClient взять
        String alias = pathAfterPrefix.split("/", 3)[1];           // "tournaments" | "user" | "competition" | ...
        // [0] пустой, потому что строка начинается с "/"

        WebClient client = clients.get(alias);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Unknown service: " + alias).getBytes());
        }

        // 3. собираем URI, НЕ удаляя сегмент alias
        String query = req.getQueryString() == null ? "" : "?" + req.getQueryString();
        String uri   = "/api/v1" + pathAfterPrefix + query;        // → "/api/v1/tournaments", "/api/v1/competition/stats/42" …

        byte[] body = client
                .method(HttpMethod.valueOf(req.getMethod()))
                .uri(uri)
                .headers(h -> Collections.list(req.getHeaderNames())
                        .forEach(n -> h.addAll(n, Collections.list(req.getHeaders(n)))))
                .retrieve()
                .bodyToMono(byte[].class)
                .block(Duration.ofSeconds(3));

        return ResponseEntity.ok(body);
    }
}
