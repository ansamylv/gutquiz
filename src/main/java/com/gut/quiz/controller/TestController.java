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

    // 1. Создание теста
    @PostMapping
    public TestResponse createTest(
            @RequestBody CreateTestRequest request,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        return testService.createTest(request, userCode);
    }

    // 2. Получение всех тестов преподавателя
    @GetMapping("/my")
    public List<TestResponse> getMyTests(
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        return testService.getMyTests(userCode);
    }

    // 3. Получение одного теста
    @GetMapping("/{id}")
    public TestResponse getTest(
            @PathVariable Long id,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        return testService.getTest(id, userCode);
    }

    // 4. Обновление теста (название/описание)
    @PutMapping("/{id}")
    public TestResponse updateTest(
            @PathVariable Long id,
            @RequestBody UpdateTestRequest request,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        return testService.updateTest(id, request, userCode);
    }

    // 5. Удаление теста
    @DeleteMapping("/{id}")
    public void deleteTest(
            @PathVariable Long id,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        testService.deleteTest(id, userCode);
    }

    // 6. Публикация теста
    @PatchMapping("/{id}/publish")
    public void publishTest(
            @PathVariable Long id,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        testService.publishTest(id, userCode);
    }

    // 7. Снятие с публикации
    @PatchMapping("/{id}/unpublish")
    public void unpublishTest(
            @PathVariable Long id,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        testService.unpublishTest(id, userCode);
    }

    // 8. Получение публичной ссылки
    @GetMapping("/{id}/link")
    public String getTestLink(
            @PathVariable Long id,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        return testService.getTestLink(id, userCode);
    }

    // 9. Получение общей статистики по тесту
    @GetMapping("/{id}/stats")
    public TestStatsResponse getTestStats(
            @PathVariable Long id,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        return testService.getTestStats(id, userCode);
    }

    // 10. Получение детальных результатов студентов
    @GetMapping("/{id}/results")
    public List<StudentAnswersResponse> getTestResults(
            @PathVariable Long id,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        return testService.getTestResults(id, userCode);
    }

    // ЭНДПОИНТЫ ДЛЯ ВОПРОСОВ
    @PostMapping("/{testId}/questions")
    public void addQuestionToTest(
            @PathVariable Long testId,
            @RequestBody CreateQuestionRequest request,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        testService.addQuestionToTest(testId, request, userCode);
    }

    @PostMapping("/{testId}/questions/batch")
    public void addQuestionsToTest(
            @PathVariable Long testId,
            @RequestBody List<CreateQuestionRequest> requests,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        testService.addQuestionsToTest(testId, requests, userCode);
    }

    // ЭНДПОИНТЫ ДЛЯ РЕДАКТИРОВАНИЯ ВОПРОСОВ
    @PutMapping("/{testId}/questions/{questionId}")
    public void updateQuestion(
            @PathVariable Long testId,
            @PathVariable Long questionId,
            @RequestBody UpdateQuestionRequest request,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        testService.updateQuestion(testId, questionId, request, userCode);
    }

    @DeleteMapping("/{testId}/questions/{questionId}")
    public void deleteQuestion(
            @PathVariable Long testId,
            @PathVariable Long questionId,
            @RequestHeader("X-User-Code") String userCode) { // <-- ДОБАВЛЕНО
        testService.deleteQuestion(testId, questionId, userCode);
    }
}
