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

    @Transactional
    public TestPassingResponse submitTestByLink(String link, TestPassingRequest request) {
        Test test = testRepository.findByPublicLinkAndIsPublishedTrue(link)
                .orElseThrow(() -> new RuntimeException("Тест не найден или не опубликован"));

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
                .correctAnswersCount(result.getCorrectAnswers())
                .totalQuestionsCount(result.getTotalQuestions())
                .message("Тест успешно сдан")
                .build();
    }

    /**
     * Вспомогательный класс для возврата результатов подсчета.
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

    private TestResult calculateResult(Test test, List<QuestionAnswer> studentAnswers) {
        // На всякий случай защищаемся от null, чтобы не было NPE при stream()
        List<QuestionAnswer> safeAnswers = studentAnswers != null ? studentAnswers : List.of();

        Map<Long, QuestionAnswer> studentAnswerMap = safeAnswers.stream()
                .collect(Collectors.toMap(QuestionAnswer::getQuestionId, answer -> answer));

        int correctAnswersCount = 0;
        int totalQuestions = test.getQuestions().size();

        for (Question question : test.getQuestions()) {
            QuestionAnswer studentAnswer = studentAnswerMap.get(question.getId());

            if (studentAnswer == null) {
                continue;
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

        double score = (totalQuestions > 0) ? ((double) correctAnswersCount / totalQuestions) * 100.0 : 0.0;

        return new TestResult(score, correctAnswersCount, totalQuestions);
    }

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

    private boolean checkTextAnswer(Question question, QuestionAnswer studentAnswer) {
        String studentText = studentAnswer.getTextAnswer();
        if (studentText == null || studentText.trim().isEmpty()) {
            return false;
        }

        String correctAnswer = question.getAnswers().stream()
                .filter(Answer::isCorrect)
                .findFirst()
                .map(Answer::getText)
                .orElse(null);

        if (correctAnswer == null) {
            return false;
        }

        return studentText.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
