package com.gut.quiz.service;

import com.gut.quiz.dto.*;
import com.gut.quiz.model.*;
import com.gut.quiz.repository.TestRepository;
import com.gut.quiz.repository.TestSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicTestService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;

    public StudentTestResponse getTestByLink(String link) {
        Test test = testRepository.findByPublicLinkAndIsPublishedTrue(link)
                .orElseThrow(() -> new RuntimeException("Тест не найден или не опубликован"));

        List<PublicQuestion> questions = test.getQuestions().stream()
                .map(this::convertToPublicQuestion)
                .collect(Collectors.toList());

        return StudentTestResponse.builder()
                .testTitle(test.getTitle())
                .testDescription(test.getDescription())
                .questions(questions)
                .build();
    }

    private PublicQuestion convertToPublicQuestion(Question question) {
        List<PublicAnswer> publicAnswers = question.getAnswers().stream()
                .map(answer -> PublicAnswer.builder()
                        .id(answer.getId())
                        .text(answer.getText())
                        .build())
                .collect(Collectors.toList());

        return PublicQuestion.builder()
                .id(question.getId())
                .text(question.getText())
                .type(question.getType().name())
                .answers(publicAnswers)
                .build();
    }

    @Transactional
    public TestPassingResponse submitTestByLink(String link, TestPassingRequest request) {
        Test test = testRepository.findByPublicLinkAndIsPublishedTrue(link)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        // РЕАЛЬНЫЙ РАСЧЕТ РЕЗУЛЬТАТА с разными типами вопросов
        TestResult result = calculateRealResult(test, request.getAnswers());

        TestSession session = new TestSession();
        session.setTest(test);
        session.setStudentFirstName(request.getStudentFirstName());
        session.setStudentLastName(request.getStudentLastName());
        session.setStudentGroup(request.getGroup());
        session.setScore(result.getScore());
        session.setCompleted(true);
        session.setStartedAt(LocalDateTime.now());
        session.setCompletedAt(LocalDateTime.now());

        testSessionRepository.save(session);

        return TestPassingResponse.builder()
                .score(result.getScore())
                .correctAnswers(result.getCorrectAnswers())
                .totalQuestions(result.getTotalQuestions())
                .resultMessage("Тест завершен! Ваш результат: " + result.getScore() + "%")
                .build();
    }

    // РЕАЛЬНЫЙ РАСЧЕТ РЕЗУЛЬТАТА С РАЗНЫМИ ТИПАМИ ВОПРОСОВ
    private TestResult calculateRealResult(Test test, List<QuestionAnswer> studentAnswers) {
        int correctCount = 0;
        int totalQuestions = test.getQuestions().size();

        for (Question question : test.getQuestions()) {
            QuestionAnswer studentAnswer = findStudentAnswer(question.getId(), studentAnswers);
            if (isAnswerCorrect(question, studentAnswer)) {
                correctCount++;
            }
        }

        double score = totalQuestions > 0 ? (double) correctCount / totalQuestions * 100 : 0;
        return new TestResult(score, correctCount, totalQuestions);
    }

    private QuestionAnswer findStudentAnswer(Long questionId, List<QuestionAnswer> studentAnswers) {
        return studentAnswers.stream()
                .filter(answer -> questionId.equals(answer.getQuestionId()))
                .findFirst()
                .orElse(null);
    }

    // ПРОВЕРКА ОТВЕТОВ ДЛЯ РАЗНЫХ ТИПОВ ВОПРОСОВ
    private boolean isAnswerCorrect(Question question, QuestionAnswer studentAnswer) {
        if (studentAnswer == null) return false;

        switch (question.getType()) {
            case SINGLE:
                return checkSingleChoice(question, studentAnswer);
            case MULTIPLE:
                return checkMultipleChoice(question, studentAnswer);
            case TEXT:
                return checkTextAnswer(question, studentAnswer);
            default:
                return false;
        }
    }

    // ДЛЯ SINGLE: должен быть выбран один правильный вариант
    private boolean checkSingleChoice(Question question, QuestionAnswer studentAnswer) {
        if (studentAnswer.getSelectedAnswerIds() == null ||
                studentAnswer.getSelectedAnswerIds().size() != 1) {
            return false;
        }

        Long selectedId = studentAnswer.getSelectedAnswerIds().get(0);
        return question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .anyMatch(correctAnswer -> correctAnswer.getId().equals(selectedId));
    }

    // ДЛЯ MULTIPLE: должны быть выбраны все правильные варианты
    private boolean checkMultipleChoice(Question question, QuestionAnswer studentAnswer) {
        if (studentAnswer.getSelectedAnswerIds() == null) return false;

        List<Long> correctAnswerIds = question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .map(Answer::getId)
                .collect(Collectors.toList());

        // Проверяем что выбраны все правильные и только правильные
        return studentAnswer.getSelectedAnswerIds().containsAll(correctAnswerIds) &&
                correctAnswerIds.containsAll(studentAnswer.getSelectedAnswerIds());
    }

    // ДЛЯ TEXT: сравниваем текстовый ответ (пока просто проверяем что ответ не пустой)
    private boolean checkTextAnswer(Question question, QuestionAnswer studentAnswer) {
        // В реальности нужно хранить правильный текстовый ответ в Question или Answer
        // Пока заглушка - считаем правильным любой непустой ответ
        return studentAnswer.getTextAnswer() != null &&
                !studentAnswer.getTextAnswer().trim().isEmpty();
    }

    // Вспомогательный класс для результатов
    private static class TestResult {
        private final double score;
        private final int correctAnswers;
        private final int totalQuestions;

        public TestResult(double score, int correctAnswers, int totalQuestions) {
            this.score = score;
            this.correctAnswers = correctAnswers;
            this.totalQuestions = totalQuestions;
        }

        public double getScore() { return score; }
        public int getCorrectAnswers() { return correctAnswers; }
        public int getTotalQuestions() { return totalQuestions; }
    }
}
