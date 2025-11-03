package com.gut.quiz.controller;

import com.gut.quiz.dto.*;
import com.gut.quiz.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping
    public TestResponse createTest(@RequestBody CreateTestRequest request) {
        return testService.createTest(request);
    }

    @GetMapping("/my")
    public List<TestResponse> getMyTests() {
        return testService.getMyTests();
    }

    @GetMapping("/{id}")
    public TestResponse getTest(@PathVariable Long id) {
        return testService.getTest(id);
    }

    @PutMapping("/{id}")
    public TestResponse updateTest(
            @PathVariable Long id,
            @RequestBody UpdateTestRequest request) {
        return testService.updateTest(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
    }

    @PatchMapping("/{id}/publish")
    public void publishTest(@PathVariable Long id) {
        testService.publishTest(id);
    }

    @PatchMapping("/{id}/unpublish")
    public void unpublishTest(@PathVariable Long id) {
        testService.unpublishTest(id);
    }

    @GetMapping("/{id}/link")
    public String getTestLink(@PathVariable Long id) {
        return testService.getTestLink(id);
    }

    @GetMapping("/{id}/stats")
    public TestStatsResponse getTestStats(@PathVariable Long id) {
        return testService.getTestStats(id);
    }

    @GetMapping("/{id}/results")
    public List<StudentAnswersResponse> getTestResults(@PathVariable Long id) {
        return testService.getTestResults(id);
    }

    // ЭНДПОИНТЫ ДЛЯ ВОПРОСОВ
    @PostMapping("/{testId}/questions")
    public void addQuestionToTest(
            @PathVariable Long testId,
            @RequestBody CreateQuestionRequest request) {
        testService.addQuestionToTest(testId, request);
    }

    @PostMapping("/{testId}/questions/batch")
    public void addQuestionsToTest(
            @PathVariable Long testId,
            @RequestBody List<CreateQuestionRequest> requests) {
        testService.addQuestionsToTest(testId, requests);
    }

    // ЭНДПОИНТЫ ДЛЯ РЕДАКТИРОВАНИЯ ВОПРОСОВ
    @PutMapping("/{testId}/questions/{questionId}")
    public void updateQuestion(
            @PathVariable Long testId,
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest request) {
        testService.updateQuestion(testId, questionId, request);
    }

    @DeleteMapping("/{testId}/questions/{questionId}")
    public void deleteQuestion(
            @PathVariable Long testId,
            @PathVariable Long questionId) {
        testService.deleteQuestion(testId, questionId);
    }
}
