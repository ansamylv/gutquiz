package com.gut.quiz.controller;

import com.gut.quiz.dto.StudentTestResponse;
import com.gut.quiz.dto.TestPassingRequest;
import com.gut.quiz.dto.TestPassingResponse;
import com.gut.quiz.service.PublicTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class PublicTestController {

    private final PublicTestService publicTestService;

    // Студент получает тест по публичной ссылке
    @GetMapping("/{link}")
    public StudentTestResponse getPublicTest(@PathVariable String link) {
        return publicTestService.getTestByLink(link);
    }

    // Студент отправляет ответы по публичной ссылке
    @PostMapping("/{link}/submit")
    public TestPassingResponse submitPublicTest(
            @PathVariable String link,
            @RequestBody TestPassingRequest request) {
        return publicTestService.submitTestByLink(link, request);
    }
}