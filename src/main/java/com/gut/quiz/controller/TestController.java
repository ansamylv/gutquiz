package com.gut.quiz.controller;

import com.gut.quiz.dto.*;
import com.gut.quiz.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    // 1. Создание теста
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestResponse createTest(@RequestBody CreateTestRequest request,
                                   @RequestHeader("X-User-Code") String teacherCode) {
        return testService.createTest(request, teacherCode);
    }

    // 2. Получение списка тестов преподавателя
    @GetMapping
    public List<TestResponse> getMyTests(@RequestHeader("X-User-Code") String teacherCode) {
        return testService.getMyTests(teacherCode);
    }

    // 3. Получение деталей одного теста
    @GetMapping("/{id}")
    public TestResponse getTestDetails(@PathVariable Long id,
                                       @RequestHeader("X-User-Code") String teacherCode) {
        return testService.getTestDetails(id, teacherCode);
    }

    // 4. Удаление теста
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTest(@PathVariable Long id,
                           @RequestHeader("X-User-Code") String teacherCode) {
        testService.deleteTest(id, teacherCode);
    }

    // 5. Добавление вопросов пакетом
    @PostMapping("/{testId}/questions/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addQuestions(@PathVariable Long testId,
                             @RequestBody List<CreateQuestionRequest> requests,
                             @RequestHeader("X-User-Code") String teacherCode) {
        testService.addQuestions(testId, requests, teacherCode);
    }

    // 6. Публикация теста
    @PatchMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publishTest(@PathVariable Long id,
                            @RequestHeader("X-User-Code") String teacherCode) {
        testService.publishTest(id, true, teacherCode);
    }

    // 7. Снятие с публикации
    @PatchMapping("/{id}/unpublish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unpublishTest(@PathVariable Long id,
                              @RequestHeader("X-User-Code") String teacherCode) {
        testService.publishTest(id, false, teacherCode);
    }

    // 8. Удаление вопроса
    @DeleteMapping("/{testId}/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable Long testId,
                               @PathVariable Long questionId,
                               @RequestHeader("X-User-Code") String teacherCode) {
        testService.deleteQuestion(testId, questionId, teacherCode);
    }


    // 9. Получение статистики по тесту
    @GetMapping("/{id}/stats")
    public TestStatsResponse getTestStats(@PathVariable Long id,
                                          @RequestHeader("X-User-Code") String teacherCode) {
        return testService.getTestStats(id, teacherCode);
    }
}
