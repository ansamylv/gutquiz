package com.gut.quiz.service;

import com.gut.quiz.dto.*;
import com.gut.quiz.model.*;
import com.gut.quiz.repository.TestRepository;
import com.gut.quiz.repository.TestSessionRepository;
import com.gut.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final TestSessionRepository testSessionRepository;

    public TestResponse createTest(CreateTestRequest request) {
        User teacher = userRepository.findAll().get(0);

        Test test = new Test();
        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setTeacher(teacher);
        test.setPublished(false);

        Test savedTest = testRepository.save(test);

        return TestResponse.builder()
                .id(savedTest.getId())
                .title(savedTest.getTitle())
                .description(savedTest.getDescription())
                .publicLink(savedTest.getPublicLink())
                .isPublished(savedTest.isPublished())
                .questions(new ArrayList<>())
                .build();
    }

    public List<TestResponse> getMyTests() {
        User teacher = userRepository.findAll().get(0);
        List<Test> tests = testRepository.findByTeacher(teacher);

        return tests.stream()
                .map(test -> TestResponse.builder()
                        .id(test.getId())
                        .title(test.getTitle())
                        .description(test.getDescription())
                        .publicLink(test.getPublicLink())
                        .isPublished(test.isPublished())
                        .questions(new ArrayList<>())
                        .build())
                .toList();
    }

    public TestResponse getTest(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        // Преобразуем вопросы в DTO
        List<QuestionDto> questionDtos = test.getQuestions().stream()
                .map(question -> QuestionDto.builder()
                        .id(question.getId())
                        .text(question.getText())
                        .type(question.getType().name())
                        .answers(question.getAnswers().stream()
                                .map(answer -> AnswerDto.builder()
                                        .id(answer.getId())
                                        .text(answer.getText())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return TestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .publicLink(test.getPublicLink())
                .isPublished(test.isPublished())
                .questions(questionDtos)
                .build();
    }

    public TestResponse updateTest(Long testId, UpdateTestRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());

        Test updatedTest = testRepository.save(test);

        return TestResponse.builder()
                .id(updatedTest.getId())
                .title(updatedTest.getTitle())
                .description(updatedTest.getDescription())
                .publicLink(updatedTest.getPublicLink())
                .isPublished(updatedTest.isPublished())
                .questions(new ArrayList<>())
                .build();
    }

    public void deleteTest(Long id) {
        testRepository.deleteById(id);
    }

    public void publishTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        test.setPublished(true);
        testRepository.save(test);
    }

    public void unpublishTest(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        test.setPublished(false);
        testRepository.save(test);
    }

    public String getTestLink(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));
        return test.getPublicLink();
    }

    public TestStatsResponse getTestStats(Long testId) {
        try {
            Test test = testRepository.findById(testId)
                    .orElseThrow(() -> new RuntimeException("Тест не найден"));

            List<TestSession> allSessions = testSessionRepository.findAll();
            List<TestSession> sessions = allSessions.stream()
                    .filter(session -> session.getTest() != null && session.getTest().getId().equals(testId))
                    .collect(Collectors.toList());

            double averageScore = sessions.stream()
                    .filter(session -> session.getIsCompleted())
                    .mapToDouble(TestSession::getScore)
                    .average()
                    .orElse(0.0);

            List<StudentResult> studentResults = sessions.stream()
                    .filter(session -> session.getIsCompleted())
                    .map(session -> StudentResult.builder()
                            .studentName(session.getStudentFirstName() + " " + session.getStudentLastName())
                            .group(session.getStudentGroup())
                            .score(session.getScore() != null ? session.getScore() : 0)
                            .completedAt(session.getCompletedAt() != null ? session.getCompletedAt() : LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            int completedCount = (int) sessions.stream().filter(session -> session.getIsCompleted()).count();

            return TestStatsResponse.builder()
                    .testTitle(test.getTitle())
                    .totalStudents(sessions.size())
                    .averageScore(Math.round(averageScore * 100.0) / 100.0)
                    .completedSessions(completedCount)
                    .activeSessions(sessions.size() - completedCount)
                    .studentResults(studentResults)
                    .build();

        } catch (Exception e) {
            System.out.println("=== DEBUG: ОШИБКА в getTestStats: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка при получении статистики: " + e.getMessage());
        }
    }

    public List<StudentAnswersResponse> getTestResults(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        List<TestSession> sessions = testSessionRepository.findByTestIdAndIsCompletedTrue(testId);

        return sessions.stream()
                .map(session -> StudentAnswersResponse.builder()
                        .studentName(session.getStudentFirstName() + " " + session.getStudentLastName())
                        .group(session.getStudentGroup())
                        .score(session.getScore())
                        .completedAt(session.getCompletedAt())
                        .answers(getStudentAnswers(session))
                        .build())
                .collect(Collectors.toList());
    }

    private List<QuestionWithAnswers> getStudentAnswers(TestSession session) {
        return List.of(
                QuestionWithAnswers.builder()
                        .questionText("Сколько будет 2 + 2?")
                        .studentAnswer("4")
                        .correctAnswer("4")
                        .isCorrect(true)
                        .build(),
                QuestionWithAnswers.builder()
                        .questionText("Столица России?")
                        .studentAnswer("Москва")
                        .correctAnswer("Москва")
                        .isCorrect(true)
                        .build()
        );
    }

    // МЕТОДЫ ДЛЯ СОЗДАНИЯ ВОПРОСОВ
    public void addQuestionToTest(Long testId, CreateQuestionRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        Question question = new Question();
        question.setText(request.getText());
        question.setType(QuestionType.valueOf(request.getType()));
        question.setTest(test);

        for (CreateAnswerRequest answerRequest : request.getAnswers()) {
            Answer answer = new Answer();
            answer.setText(answerRequest.getText());
            answer.setCorrect(answerRequest.isCorrect());
            answer.setQuestion(question);
            question.getAnswers().add(answer);
        }

        test.getQuestions().add(question);
        testRepository.save(test);
    }

    public void addQuestionsToTest(Long testId, List<CreateQuestionRequest> questions) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        for (CreateQuestionRequest questionRequest : questions) {
            Question question = new Question();
            question.setText(questionRequest.getText());
            question.setType(QuestionType.valueOf(questionRequest.getType()));
            question.setTest(test);

            for (CreateAnswerRequest answerRequest : questionRequest.getAnswers()) {
                Answer answer = new Answer();
                answer.setText(answerRequest.getText());
                answer.setCorrect(answerRequest.isCorrect());
                answer.setQuestion(question);
                question.getAnswers().add(answer);
            }

            test.getQuestions().add(question);
        }

        testRepository.save(test);
    }

    // МЕТОДЫ ДЛЯ РЕДАКТИРОВАНИЯ ВОПРОСОВ
    public void updateQuestion(Long testId, Long questionId, UpdateQuestionRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        Question question = test.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Вопрос не найден"));

        question.setText(request.getText());
        question.setType(QuestionType.valueOf(request.getType()));

        updateAnswers(question, request.getAnswers());

        testRepository.save(test);
    }

    // МЕТОД ДЛЯ УДАЛЕНИЯ ВОПРОСОВ - ДОБАВЛЕН
    public void deleteQuestion(Long testId, Long questionId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        boolean removed = test.getQuestions().removeIf(q -> q.getId().equals(questionId));

        if (!removed) {
            throw new RuntimeException("Вопрос не найден");
        }

        testRepository.save(test);
    }

    private void updateAnswers(Question question, List<UpdateAnswerRequest> answerRequests) {
        question.getAnswers().clear();

        for (UpdateAnswerRequest answerRequest : answerRequests) {
            Answer answer = new Answer();
            answer.setText(answerRequest.getText());
            answer.setCorrect(answerRequest.isCorrect());
            answer.setQuestion(question);
            question.getAnswers().add(answer);
        }
    }

    private void updateQuestions(Test test, List<com.gut.quiz.dto.UpdateQuestion> updateQuestions) {
        // Заглушка для будущей реализации
    }
}
