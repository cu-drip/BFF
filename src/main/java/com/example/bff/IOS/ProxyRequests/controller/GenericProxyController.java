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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@RestController("webProxyController")
@RequestMapping("/api/v1")
public class GenericProxyController {

    private final Map<String, WebClient> clients;
    private static final Set<String> SKIP_HEADERS = Set.of(
            HttpHeaders.HOST, HttpHeaders.CONTENT_LENGTH            // не пересылаем эти хедеры
    );

    public GenericProxyController(
            @Qualifier("userWebClient")        WebClient userClient,
            @Qualifier("competitionWebClient") WebClient competitionClient,
            @Qualifier("feedbackWebClient")    WebClient feedbackClient,
            @Qualifier("chatWebClient")        WebClient chatClient,
            @Qualifier("engineWebClient")      WebClient engineClient,
            @Qualifier("statisticWebClient") WebClient statisticClient
    ) {
        this.clients = Map.of(
                "users",       userClient,
                "auth",        userClient,
                "feedback",    feedbackClient,
                "chat",        chatClient,
                "tournaments", competitionClient,
                "teams",       competitionClient,
                "tour",        engineClient,
                "matches",     engineClient,
                "bracket",     engineClient,
                "stats", statisticClient
        );
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest req) {

        /* ---------- вычисляем alias ---------- */
        String afterPrefix = req.getRequestURI().replaceFirst("/api/v1", "");
        String[] parts = afterPrefix.split("/", 3);          // ["", "tour", …] или ["", "chat", …]

        if (parts.length < 2 || parts[1].isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Path is missing service alias".getBytes(StandardCharsets.UTF_8));
        }

        String alias = parts[1];                             // "tour", "users", …
        WebClient client = clients.get(alias);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Unknown service: " + alias).getBytes(StandardCharsets.UTF_8));
        }

        /* ---------- собираем целевой URI ---------- */
        String query = req.getQueryString() == null ? "" : "?" + req.getQueryString();
        String uri   = "/api/v1" + afterPrefix + query;      // → /api/v1/tour

        /* ---------- читаем тело запроса ---------- */
        byte[] requestBody = new byte[0];
        try {
            if (req.getContentLengthLong() != 0) {
                requestBody = StreamUtils.copyToByteArray(req.getInputStream());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Cannot read body: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }

        /* ---------- проксируем ---------- */
        WebClient.RequestHeadersSpec<?> spec = client
                .method(HttpMethod.valueOf(req.getMethod()))
                .uri(uri)
                .headers(h -> Collections.list(req.getHeaderNames())
                        .stream()
                        .filter(hn -> !SKIP_HEADERS.contains(hn))
                        .forEach(hn -> h.addAll(hn, Collections.list(req.getHeaders(hn)))));

        // только если тело реально есть и метод допускает его
        if (requestBody.length > 0 && methodAllowsBody(req.getMethod())) {
            spec = ((WebClient.RequestBodySpec) spec).bodyValue(requestBody);
        }

        ResponseEntity<byte[]> upstream = spec
                .exchangeToMono(r -> r.toEntity(byte[].class))
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(("Upstream error: "+ex.getMessage())
                                .getBytes(StandardCharsets.UTF_8))))
                .block();

        return upstream;
    }

    /* методы, где тело не разрешено */
    private static boolean methodAllowsBody(String m) {
        return switch (m) {
            case "GET", "HEAD", "OPTIONS", "TRACE" -> false;
            default -> true;
        };
    }
}