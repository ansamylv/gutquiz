package com.gut.quiz.service;

import com.gut.quiz.dto.*;
import com.gut.quiz.model.*;
import com.gut.quiz.repository.TestRepository;
import com.gut.quiz.repository.TestSessionRepository;
import com.gut.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final TestSessionRepository testSessionRepository;

    // ======================================================================================
    //                           АВТОРИЗАЦИЯ И ПРОВЕРКИ
    // ======================================================================================

    private User findTeacher(String teacherCode) {
        return userRepository.findByCode(teacherCode)
                .orElseThrow(() -> new RuntimeException("Преподаватель с кодом " + teacherCode + " не найден."));
    }

    private Test findTestAndCheckOwner(Long testId, String teacherCode) {
        User teacher = findTeacher(teacherCode);
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Тест с ID " + testId + " не найден."));

        if (!test.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("У вас нет прав для доступа к этому тесту.");
        }
        return test;
    }

    // ======================================================================================
    //                           МЕТОДЫ УЧИТЕЛЯ (CRUD, УПРАВЛЕНИЕ)
    // ======================================================================================

    @Transactional
    public TestResponse createTest(CreateTestRequest request, String teacherCode) {
        User teacher = findTeacher(teacherCode);

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

    public List<TestResponse> getMyTests(String teacherCode) {
        User teacher = findTeacher(teacherCode);

        return testRepository.findByTeacher(teacher).stream()
                .map(this::mapToTestResponse)
                .collect(Collectors.toList());
    }

    private TestResponse mapToTestResponse(Test test) {
        return TestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .publicLink(test.getPublicLink())
                .isPublished(test.isPublished())
                .questions(new ArrayList<>())
                .build();
    }

    public TestResponse getTestDetails(Long id, String teacherCode) {
        Test test = findTestAndCheckOwner(id, teacherCode);

        List<QuestionDto> questionDtos = test.getQuestions().stream()
                .map(this::mapToQuestionDto)
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

    private QuestionDto mapToQuestionDto(Question question) {
        List<AnswerDto> answerDtos = question.getAnswers().stream()
                .map(answer -> AnswerDto.builder()
                        .id(answer.getId())
                        .text(answer.getText())
                        .isCorrect(answer.isCorrect())
                        .build())
                .collect(Collectors.toList());

        return QuestionDto.builder()
                .id(question.getId())
                .text(question.getText())
                .type(question.getType().name())
                .answers(answerDtos)
                .build();
    }

    public void deleteTest(Long id, String teacherCode) {
        Test test = findTestAndCheckOwner(id, teacherCode);
        testRepository.delete(test);
    }

    @Transactional
    public void publishTest(Long id, boolean publish, String teacherCode) {
        Test test = findTestAndCheckOwner(id, teacherCode);

        if (publish && test.getQuestions().isEmpty()) {
            throw new RuntimeException("Нельзя опубликовать тест без вопросов.");
        }
        test.setPublished(publish);
        testRepository.save(test);
    }

    @Transactional
    public void addQuestions(Long testId, List<CreateQuestionRequest> requests, String teacherCode) {
        Test test = findTestAndCheckOwner(testId, teacherCode);

        List<Question> newQuestions = requests.stream()
                .map(request -> mapToQuestion(request, test))
                .collect(Collectors.toList());

        test.getQuestions().addAll(newQuestions);
        testRepository.save(test);
    }

    private Question mapToQuestion(CreateQuestionRequest request, Test test) {
        Question question = new Question();
        question.setTest(test);
        question.setText(request.getText());
        question.setType(QuestionType.valueOf(request.getType()));

        if (request.getAnswers() != null) {
            request.getAnswers().forEach(answerRequest -> {
                Answer answer = new Answer();
                answer.setText(answerRequest.getText());
                answer.setCorrect(answerRequest.isCorrect());
                question.addAnswer(answer);
            });
        }
        return question;
    }

    @Transactional
    public void deleteQuestion(Long testId, Long questionId, String teacherCode) {
        Test test = findTestAndCheckOwner(testId, teacherCode);

        boolean removed = test.getQuestions().removeIf(q -> q.getId().equals(questionId));

        if (!removed) {
            throw new RuntimeException("Вопрос не найден.");
        }

        testRepository.save(test);
    }

    // 9. Получение статистики теста
    public TestStatsResponse getTestStats(Long id, String teacherCode) {
        Test test = findTestAndCheckOwner(id, teacherCode);

        // 1. Считаем только ЗАВЕРШЕННЫЕ сессии
        int completedSessions = testSessionRepository.countByTestIdAndIsCompletedTrue(id);

        // 2. Считаем ВСЕ сессии (для определения активных)
        int allSessionsCount = testSessionRepository.countByTestId(id);

        // 3. Вычисляем активные сессии (незавершенные)
        int activeSessions = allSessionsCount - completedSessions;

        // 4. Находим средний балл только по ЗАВЕРШЕННЫМ сессиям
        Double averageScore = testSessionRepository.findAverageScoreByTest(test);

        // 5. ИСПРАВЛЕНИЕ: Получаем результаты ТОЛЬКО ЗАВЕРШЕННЫХ сессий
        List<TestSession> completedSessionsList = testSessionRepository.findByTestIdAndIsCompletedTrue(id);

        List<StudentResult> results = completedSessionsList.stream()
                .map(session -> StudentResult.builder()
                        .studentName(session.getStudentFirstName() + " " + session.getStudentLastName())
                        .group(session.getStudentGroup())
                        .score(session.getScore() != null ? session.getScore() : 0.0)
                        .completedAt(session.getCompletedAt())
                        .build())
                .collect(Collectors.toList());

        return TestStatsResponse.builder()
                .testTitle(test.getTitle())
                .totalStudents(completedSessions)
                .completedSessions(completedSessions)
                .averageScore(averageScore != null ? averageScore : 0.0)
                .activeSessions(activeSessions)
                .studentResults(results)
                .build();
    }
}