package com.gut.quiz.controller;

import com.gut.quiz.dto.UserStats;
import com.gut.quiz.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader; // <-- НОВЫЙ ИМПОРТ
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public UserStats getCurrentUserStats(
            @RequestHeader("X-User-Code") String userCode) {

        return statsService.getCurrentUserStats(userCode);
    }
}
