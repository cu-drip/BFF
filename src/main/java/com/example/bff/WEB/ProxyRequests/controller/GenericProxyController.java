package com.example.bff.WEB.ProxyRequests.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static java.util.Map.entry;

@RestController("iosProxyController")
@RequestMapping("/api/v1web")
public class GenericProxyController {

    private final Map<String, WebClient> clients;
    private static final Set<String> SKIP_HEADERS =
            Set.of(HttpHeaders.HOST, HttpHeaders.CONTENT_LENGTH, HttpHeaders.TRANSFER_ENCODING);

    public GenericProxyController(
            @Qualifier("userWebClient")        WebClient userClient,
            @Qualifier("competitionWebClient") WebClient competitionClient,
            @Qualifier("feedbackWebClient")    WebClient feedbackClient,
            @Qualifier("chatWebClient")        WebClient chatClient,
            @Qualifier("engineWebClient")      WebClient engineClient,
            @Qualifier("statisticWebClient")   WebClient statisticClient
    ) {
        this.clients = Map.ofEntries(
                entry("users",       userClient),
                entry("auth",        userClient),
                entry("feedback",    feedbackClient),
                entry("chats",       chatClient),
                entry("tournaments", competitionClient),
                entry("teams",       competitionClient),
                entry("tour",        engineClient),
                entry("matches",     engineClient),
                entry("bracket",     engineClient),
                entry("stats",       statisticClient),
                entry("admin",       feedbackClient)   // ← 11-я пара
        );
    }


    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest req) {

        /* ---------- alias и целевой WebClient ---------- */
        String pathAfterPrefix = req.getRequestURI().replaceFirst("/api/v1web", "");
        String[] parts = pathAfterPrefix.split("/", 3);      // ["", "tournaments", ...]
        if (parts.length < 2) {
            return ResponseEntity.badRequest()
                    .body("Path is missing service alias".getBytes(StandardCharsets.UTF_8));
        }

        String alias = parts[1];
        WebClient client = clients.get(alias);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Unknown service: " + alias).getBytes(StandardCharsets.UTF_8));
        }

        String query = req.getQueryString() == null ? "" : "?" + req.getQueryString();
        String uri   = "/api/v1" + pathAfterPrefix + query;

        /* ---------- читаем тело запроса (если есть) ---------- */
        byte[] requestBody = new byte[0];
        try {
            if (req.getContentLengthLong() != 0) {
                requestBody = StreamUtils.copyToByteArray(req.getInputStream());
            }
        } catch (IOException io) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(("Cannot read body: " + io.getMessage()).getBytes(StandardCharsets.UTF_8));
        }

        /* ---------- собираем и отправляем ---------- */
        WebClient.RequestHeadersSpec<?> spec = client
                .method(HttpMethod.valueOf(req.getMethod()))
                .uri(uri)
                .headers(h -> Collections.list(req.getHeaderNames()).stream()
                        .filter(n -> !SKIP_HEADERS.contains(n))
                        .forEach(n -> h.addAll(n, Collections.list(req.getHeaders(n)))));

        if (requestBody.length > 0 && methodAllowsBody(req.getMethod())) {
            spec = ((WebClient.RequestBodySpec) spec).bodyValue(requestBody);
        }

        byte[] body = spec
                .retrieve()
                .bodyToMono(byte[].class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(ex -> Mono.just(("Upstream error: " + ex.getMessage())
                        .getBytes(StandardCharsets.UTF_8)))
                .block();

        return ResponseEntity.ok(body);
    }

    /** Для каких HTTP-методов допустимо тело */
    private static boolean methodAllowsBody(String m) {
        return switch (m) {
            case "GET", "HEAD", "OPTIONS", "TRACE" -> false;
            default                                -> true;
        };
    }
}
