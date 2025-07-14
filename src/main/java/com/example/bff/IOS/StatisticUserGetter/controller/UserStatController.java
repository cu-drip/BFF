package com.example.bff.IOS.StatisticUserGetter.controller;

import com.example.bff.IOS.StatisticUserGetter.model.UserStatDto;
import com.example.bff.IOS.StatisticUserGetter.service.UserStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1spec/user/stat")
@RequiredArgsConstructor            // генерирует конструктор с одним параметром service
public class UserStatController {

    private final UserStatService service;   // больше НИКАКИХ конструкторов вручную!

    @GetMapping("/{userId}")
    public List<UserStatDto> getUserStats(@PathVariable UUID userId) {
        return service.getUserStats(userId).block(); // блокируем реактивную цепочку
    }
}
