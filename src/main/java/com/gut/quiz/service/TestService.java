package com.gut.quiz.service;

import com.gut.quiz.dto.*;
import com.gut.quiz.model.*;
import com.gut.quiz.repository.TestRepository;
import com.gut.quiz.repository.TestSessionRepository;
import com.gut.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * Вспомогательный метод для получения текущего преподавателя
     */
    private User getTeacher(String userCode) {
        return userRepository.findByCode(userCode)
                .orElseThrow(() -> new RuntimeException("Преподаватель не найден. Невозможно выполнить операцию."));
    }

    /**
     * Вспомогательный метод для получения теста и проверки принадлежности
     */
    private Test getTestAndCheckAuthorization(Long testId, String userCode) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест не найден"));

        // *** ПРОВЕРКА АВТОРИЗАЦИИ ***
        if (!test.getTeacher().getCode().equals(userCode)) {
            throw new RuntimeException("Доступ запрещен. Тест не принадлежит текущему пользователю.");
        }
        return test;
    }

    // =========================================================================
    // ОПЕРАЦИИ С ТЕСТАМИ
    // =========================================================================

    @Transactional
    public TestResponse createTest(CreateTestRequest request, String userCode) {
        User teacher = getTeacher(userCode); // <-- Замена заглушки

        Test test = new Test();
        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setTeacher(teacher); // <-- Привязываем к реальному преподавателю
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

    @Transactional(readOnly = true)
    public List<TestResponse> getMyTests(String userCode) {
        User teacher = getTeacher(userCode); // <-- Замена заглушки
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

    @Transactional(readOnly = true)
    public TestResponse getTest(Long id, String userCode) {
        Test test = getTestAndCheckAuthorization(id, userCode); // <-- Проверка авторизации

        // Преобразуем вопросы в DTO
        List<QuestionDto> questionDtos = test.getQuestions().stream()
                .map(question -> QuestionDto.builder()
                        .id(question.getId())
                        .text(question.getText())
                        .type(question.getType().name())
                        // В DTO для преподавателя можно вернуть ID ответов
                        .answers(question.getAnswers().stream()
                                .map(answer -> AnswerDto.builder()
                                        .id(answer.getId())
                                        .text(answer.getText())
                                        .isCorrect(answer.isCorrect()) // Учителю показываем правильность
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

    @Transactional
    public TestResponse updateTest(Long testId, UpdateTestRequest request, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации

        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());

        Test updatedTest = testRepository.save(test);

        return TestResponse.builder()
                .id(updatedTest.getId())
                .title(updatedTest.getTitle())
                .description(updatedTest.getDescription())
                .publicLink(updatedTest.getPublicLink())
                .isPublished(updatedTest.isPublished())
                // Вопросы при обновлении теста обычно обновляются через отдельные эндпоинты
                .questions(new ArrayList<>())
                .build();
    }

    @Transactional
    public void deleteTest(Long id, String userCode) {
        getTestAndCheckAuthorization(id, userCode); // <-- Проверка авторизации
        testRepository.deleteById(id);
    }

    @Transactional
    public void publishTest(Long testId, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации
        test.setPublished(true);
        testRepository.save(test);
    }

    @Transactional
    public void unpublishTest(Long testId, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации
        test.setPublished(false);
        testRepository.save(test);
    }

    @Transactional(readOnly = true)
    public String getTestLink(Long testId, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации
        return test.getPublicLink();
    }

    // =========================================================================
    // СТАТИСТИКА И РЕЗУЛЬТАТЫ
    // =========================================================================

    @Transactional(readOnly = true)
    public TestStatsResponse getTestStats(Long testId, String userCode) {
        try {
            Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации

            // *** ВНИМАНИЕ: ЗДЕСЬ ИСПОЛЬЗУЕТСЯ ТВОЯ ПРЕДЫДУЩАЯ ЛОГИКА ***
            // В идеале sessions нужно получать через репозиторий, используя testSessionRepository.findByTest(test)
            List<TestSession> allSessions = testSessionRepository.findAll();
            List<TestSession> sessions = allSessions.stream()
                    .filter(session -> session.getTest() != null && session.getTest().getId().equals(testId))
                    .collect(Collectors.toList());

            double averageScore = sessions.stream()
                    .filter(TestSession::getIsCompleted)
                    .mapToDouble(TestSession::getScore)
                    .average()
                    .orElse(0.0);

            List<StudentResult> studentResults = sessions.stream()
                    .filter(TestSession::getIsCompleted)
                    .map(session -> StudentResult.builder()
                            .studentName(session.getStudentFirstName() + " " + session.getStudentLastName())
                            .group(session.getStudentGroup())
                            .score(session.getScore() != null ? session.getScore() : 0)
                            .completedAt(session.getCompletedAt() != null ? session.getCompletedAt() : LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            int completedCount = (int) studentResults.size(); // Уже отфильтрованы по isCompleted

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

    @Transactional(readOnly = true)
    public List<StudentAnswersResponse> getTestResults(Long testId, String userCode) {
        getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации

        // *** ВНИМАНИЕ: findByTestIdAndIsCompletedTrue не определен в твоем репозитории
        // Используем findByTestId
        List<TestSession> sessions = testSessionRepository.findByTestId(testId).stream()
                .filter(TestSession::getIsCompleted)
                .toList();

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

    // МЕТОД ДЛЯ ЗАГЛУШКИ ОТВЕТОВ СТУДЕНТОВ (ТРЕБУЕТ ДОРАБОТКИ)
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

    // =========================================================================
    // ОПЕРАЦИИ С ВОПРОСАМИ
    // =========================================================================

    @Transactional
    public void addQuestionToTest(Long testId, CreateQuestionRequest request, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации

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

    @Transactional
    public void addQuestionsToTest(Long testId, List<CreateQuestionRequest> questions, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации

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

    @Transactional
    public void updateQuestion(Long testId, Long questionId, UpdateQuestionRequest request, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации

        Question question = test.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Вопрос не найден"));

        question.setText(request.getText());
        question.setType(QuestionType.valueOf(request.getType()));

        updateAnswers(question, request.getAnswers());

        testRepository.save(test);
    }

    @Transactional
    public void deleteQuestion(Long testId, Long questionId, String userCode) {
        Test test = getTestAndCheckAuthorization(testId, userCode); // <-- Проверка авторизации

        boolean removed = test.getQuestions().removeIf(q -> q.getId().equals(questionId));

        if (!removed) {
            throw new RuntimeException("Вопрос не найден");
        }

        testRepository.save(test);
    }

    // =========================================================================
    // Вспомогательные методы
    // =========================================================================

    private void updateAnswers(Question question, List<UpdateAnswerRequest> answerRequests) {
        // Удаляем все старые ответы
        question.getAnswers().clear();

        // Добавляем новые ответы
        for (UpdateAnswerRequest answerRequest : answerRequests) {
            Answer answer = new Answer();
            answer.setText(answerRequest.getText());
            answer.setCorrect(answerRequest.isCorrect());
            answer.setQuestion(question);
            question.getAnswers().add(answer);
        }
    }
}
