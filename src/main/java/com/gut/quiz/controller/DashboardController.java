package com.gut.quiz.controller;

import com.gut.quiz.dto.DashboardDTO;
import com.gut.quiz.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/data")
    public ResponseEntity<DashboardDTO> getDashboardData(@RequestParam String teacherCode) {
        DashboardDTO data = dashboardService.getDashboardData(teacherCode);
        return ResponseEntity.ok(data);
    }
}
