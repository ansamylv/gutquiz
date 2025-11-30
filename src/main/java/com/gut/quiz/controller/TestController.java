package com.gut.quiz.controller;

import com.gut.quiz.dto.CreateTestRequest;
import com.gut.quiz.dto.TestResponse;
import com.gut.quiz.dto.UpdateTestRequest;
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

    @GetMapping("/{id}")
    public ResponseEntity<TestResponse> getTest(
            @PathVariable("id") Long id,
            @RequestParam("teacherCode") String teacherCode) {
        TestResponse response = testService.getTestDetails(id, teacherCode);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestResponse> updateTest(
            @PathVariable("id") Long id,
            @RequestBody UpdateTestRequest request,
            @RequestParam("teacherCode") String teacherCode) {
        TestResponse response = testService.updateTest(id, request, teacherCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<TestResponse> addQuestions(
            @PathVariable("id") Long id,
            @RequestBody java.util.List<com.gut.quiz.dto.CreateQuestionRequest> requests,
            @RequestParam("teacherCode") String teacherCode) {
        testService.addQuestions(id, requests, teacherCode);
        TestResponse response = testService.getTestDetails(id, teacherCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<TestResponse> publishTest(
            @PathVariable("id") Long id,
            @RequestParam("teacherCode") String teacherCode,
            @RequestParam(value = "publish", defaultValue = "true") boolean publish) {
        testService.publishTest(id, publish, teacherCode);
        TestResponse response = testService.getTestDetails(id, teacherCode);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable("id") Long id,
            @PathVariable("questionId") Long questionId,
            @RequestParam("teacherCode") String teacherCode) {
        testService.deleteQuestion(id, questionId, teacherCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<Void> finishTest(
            @PathVariable("id") Long id,
            @RequestParam("teacherCode") String teacherCode) {
        testService.finishTest(id, teacherCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(
            @PathVariable("id") Long id,
            @RequestParam("teacherCode") String teacherCode) {
        testService.deleteTest(id, teacherCode);
        return ResponseEntity.noContent().build();
    }
}
