package com.example.bff.IOS.ProxyRequests.controller;

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

@RestController("webProxyController")
@RequestMapping("/api/v1")
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

        String afterPrefix = req.getRequestURI().replaceFirst("/api/v1", "");
        String[] parts     = afterPrefix.split("/", 3);   // ["", "tournaments", ...]  или ["", "user", ...]
        if (parts.length < 2) {   // запрос был только "/api/v1"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Path is missing service alias".getBytes());
        }

        String alias = parts[1];                 // tournaments / user / …
        WebClient client = clients.get(alias);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Unknown service: " + alias).getBytes());
        }

        String query = req.getQueryString() == null ? "" : "?" + req.getQueryString();
        String uri   = "/api/v1" + afterPrefix + query;   // отправляем хвост целиком

        /* ---- главное изменение ---- */
        ResponseEntity<byte[]> upstream = client
                .method(HttpMethod.valueOf(req.getMethod()))
                .uri(uri)
                .headers(h -> Collections.list(req.getHeaderNames())
                        .forEach(n -> h.addAll(n, Collections.list(req.getHeaders(n)))))
                .exchangeToMono(r -> r.toEntity(byte[].class))   // сохраняем status/body/headers
                .block(Duration.ofSeconds(5));
        /* --------------------------- */

        return upstream;          // отдаём фронту ровно то, что вернул сервис
    }
}
