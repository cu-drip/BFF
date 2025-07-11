// src/main/java/com/example/bff/controller/PingController.java
package com.example.bff.IOS.StatisticOfTournament1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
