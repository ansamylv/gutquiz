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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicTestService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;

    // ... (методы getTestByLink и convertToPublicQuestion остаются без изменений) ...
    public StudentTestResponse getTestByLink(String link) {
        Test test = testRepository.findByPublicLinkAndIsPublishedTrue(link)
                .orElseThrow(() -> new RuntimeException("Тест не найден или не опубликован"));

        List<PublicQuestion> questions = test.getQuestions().stream()
                .map(this::convertToPublicQuestion)
                .collect(Collectors.<PublicQuestion>toList());

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
    // ... (конец неизменных методов) ...


    @Transactional
    public TestPassingResponse submitTestByLink(String link, TestPassingRequest request) {
        Test test = testRepository.findByPublicLinkAndIsPublishedTrue(link)
                .orElseThrow(() -> new RuntimeException("Тест не найден или не опубликован"));

        // 🏆 ВЫЗОВ РЕАЛЬНОЙ ЛОГИКИ (ЗАГЛУШКА УДАЛЕНА)
        TestResult result = calculateResult(test, request.getAnswers());

        TestSession session = new TestSession();
        session.setTest(test);
        session.setStudentFirstName(request.getStudentFirstName());
        session.setStudentLastName(request.getStudentLastName());
        session.setStudentGroup(request.getGroup());
        session.setScore(result.getScore());

        session.setIsCompleted(true);
        session.setCompletedAt(LocalDateTime.now());

        testSessionRepository.save(session);

        return TestPassingResponse.builder()
                .score(result.getScore())
                // 🏆 ЗАГЛУШКИ УДАЛЕНЫ (используются реальные данные из result)
                .correctAnswersCount(result.getCorrectAnswers())
                .totalQuestionsCount(result.getTotalQuestions())
                .message("Тест успешно сдан")
                .build();
    }

    /**
     * Внутренний вспомогательный класс для возврата результатов подсчета.
     */
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

    /**
     * 🏆 РЕАЛЬНАЯ ЛОГИКА ПОДСЧЕТА БАЛЛОВ (ЗАГЛУШКА УДАЛЕНА)
     */
    private TestResult calculateResult(Test test, List<QuestionAnswer> studentAnswers) {
        // Создаем карту ответов студента для быстрого доступа по ID вопроса
        Map<Long, QuestionAnswer> studentAnswerMap = studentAnswers.stream()
                .collect(Collectors.toMap(QuestionAnswer::getQuestionId, answer -> answer));

        int correctAnswersCount = 0;
        int totalQuestions = test.getQuestions().size();

        // Проходим по каждому вопросу из базы данных
        for (Question question : test.getQuestions()) {
            QuestionAnswer studentAnswer = studentAnswerMap.get(question.getId());

            if (studentAnswer == null) {
                continue; // Студент пропустил вопрос, ответ не засчитан
            }

            boolean isCorrect = false;
            switch (question.getType()) {
                case SINGLE:
                    isCorrect = checkSingleAnswer(question, studentAnswer);
                    break;
                case MULTIPLE:
                    isCorrect = checkMultipleAnswer(question, studentAnswer);
                    break;
                case TEXT:
                    isCorrect = checkTextAnswer(question, studentAnswer);
                    break;
            }

            if (isCorrect) {
                correctAnswersCount++;
            }
        }

        // Расчет процента
        double score = (totalQuestions > 0) ? ((double) correctAnswersCount / totalQuestions) * 100.0 : 0.0;

        return new TestResult(score, correctAnswersCount, totalQuestions);
    }

    // Логика для SINGLE (без изменений)
    private boolean checkSingleAnswer(Question question, QuestionAnswer studentAnswer) {
        Long correctAnswerId = question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .map(Answer::getId)
                .findFirst()
                .orElse(null);

        return correctAnswerId != null &&
                studentAnswer.getSelectedAnswerIds() != null &&
                studentAnswer.getSelectedAnswerIds().size() == 1 &&
                studentAnswer.getSelectedAnswerIds().get(0).equals(correctAnswerId);
    }

    // Логика для MULTIPLE (без изменений)
    private boolean checkMultipleAnswer(Question question, QuestionAnswer studentAnswer) {
        List<Long> correctAnswerIds = question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .map(Answer::getId)
                .collect(Collectors.toList());

        if (correctAnswerIds.isEmpty()) {
            return false;
        }

        List<Long> selectedIds = studentAnswer.getSelectedAnswerIds() != null ? studentAnswer.getSelectedAnswerIds() : new ArrayList<>();

        return selectedIds.containsAll(correctAnswerIds) &&
                correctAnswerIds.containsAll(selectedIds);
    }

    /**
     * 🏆 РЕАЛЬНАЯ ЛОГИКА ПРОВЕРКИ ТЕКСТА (ЗАГЛУШКА УДАЛЕНА)
     */
    private boolean checkTextAnswer(Question question, QuestionAnswer studentAnswer) {
        // Ответ студента
        String studentText = studentAnswer.getTextAnswer();
        if (studentText == null || studentText.trim().isEmpty()) {
            return false;
        }

        // Правильный ответ из базы данных (первый найденный 'isCorrect' ответ)
        String correctAnswer = question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .findFirst()
                .map(Answer::getText)
                .orElse(null);

        if (correctAnswer == null) {
            // Вопрос настроен некорректно (нет правильного ответа)
            return false;
        }

        // Сравнение без учета регистра и пробелов по краям
        return studentText.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
