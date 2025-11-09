package com.gut.quiz.controller;

import com.gut.quiz.dto.CreateTestRequest;
import com.gut.quiz.dto.TestResponse;
import com.gut.quiz.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller для управления тестами преподавателя (CRUD).
 */
@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    /**
     * Создание нового теста.
     * Соответствует запросу из Vue: POST /api/tests?teacherCode=...
     */
    @PostMapping
    public ResponseEntity<TestResponse> createTest(
            @RequestBody CreateTestRequest request,
            @RequestParam String teacherCode) {

        // Используем метод, который вы уже реализовали в TestService
        TestResponse response = testService.createTest(request, teacherCode);
        // Возвращаем статус 201 Created и детали нового теста
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Здесь позже будут: GET /api/tests/{id}, DELETE /api/tests/{id}, PUT /api/tests/{id}
}
