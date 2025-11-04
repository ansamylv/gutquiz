package com.gut.quiz.controller;

import com.gut.quiz.dto.DashboardDTO;
import com.gut.quiz.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Читаем код преподавателя из заголовка X-User-Code
    @GetMapping
    public DashboardDTO getDashboardData(
            @RequestHeader("X-User-Code") String userCode) {

        return dashboardService.getDashboardData(userCode);
    }
}
