package com.gut.quiz.service;

import com.gut.quiz.dto.*;
import com.gut.quiz.model.Test;
import com.gut.quiz.model.Question;
import com.gut.quiz.model.Answer;
import com.gut.quiz.model.TestSession;
import com.gut.quiz.repository.TestRepository;
import com.gut.quiz.repository.TestSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;

    public StudentTestResponse getTestForStudent(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        return StudentTestResponse.builder()
                .testTitle(test.getTitle())
                .testDescription(test.getDescription())
                .questions(mapQuestions(test.getQuestions()))
                .build();
    }

    public TestPassingResponse submitTest(TestPassingRequest request) {
        // Здесь будет логика проверки ответов и подсчета баллов
        TestSession session = new TestSession();
        session.setStudentFirstName(request.getStudentFirstName());
        session.setStudentLastName(request.getStudentLastName());
        session.setStudentGroup(request.getGroup());
        session.setScore(calculateScore(request));
        session.setCompleted(true);
        session.setStartedAt(LocalDateTime.now());
        session.setCompletedAt(LocalDateTime.now());

        testSessionRepository.save(session);

        return TestPassingResponse.builder()
                .score(session.getScore())
                .correctAnswers(3) // заглушка
                .totalQuestions(5) // заглушка
                .resultMessage("Тест завершен! Ваш результат: " + session.getScore() + "%")
                .build();
    }

    private double calculateScore(TestPassingRequest request) {
        // Заглушка - всегда 80%
        return 80.0;
    }

    private List<PublicQuestion> mapQuestions(List<Question> questions) {
        List<PublicQuestion> result = new ArrayList<>();
        for (Question question : questions) {
            result.add(PublicQuestion.builder()
                    .id(question.getId())
                    .text(question.getText())
                    .type(question.getType().name())
                    .answers(mapAnswers(question.getAnswers()))
                    .build());
        }
        return result;
    }

    private List<PublicAnswer> mapAnswers(List<Answer> answers) {
        List<PublicAnswer> result = new ArrayList<>();
        for (Answer answer : answers) {
            result.add(PublicAnswer.builder()
                    .id(answer.getId())
                    .text(answer.getText())
                    .build());
        }
        return result;
    }
}