package com.gut.quiz.service;

import com.gut.quiz.dto.DashboardDTO;
import com.gut.quiz.dto.TestSummary;
import com.gut.quiz.model.Test;
import com.gut.quiz.model.User;
import com.gut.quiz.repository.TestRepository;
import com.gut.quiz.repository.TestSessionRepository;
import com.gut.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;
    private final UserRepository userRepository;

    /**
     * Вспомогательный метод для получения текущего преподавателя
     */
    private User getTeacher(String userCode) {
        return userRepository.findByCode(userCode)
                .orElseThrow(() -> new RuntimeException("Преподаватель не найден. Невозможно получить данные дашборда."));
    }

    @Transactional(readOnly = true)
    // Метод теперь принимает userCode
    public DashboardDTO getDashboardData(String userCode) {
        User teacher = getTeacher(userCode); // <-- Замена заглушки!

        int totalTests = testRepository.countByTeacher(teacher);
        int totalStudents = testSessionRepository.countDistinctStudentsByTeacher(teacher);
        Double averageScore = testSessionRepository.findAverageScoreByTeacher(teacher);

        // Получаем все тесты и сортируем по дате создания, чтобы взять недавние
        List<Test> tests = testRepository.findByTeacher(teacher);
        List<TestSummary> recentTests = tests.stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt())) // Сортируем по убыванию даты
                .map(this::mapToTestSummary)
                .limit(3)
                .toList();

        return DashboardDTO.builder()
                .teacherName(teacher.getFirstName() + " " + teacher.getLastName())
                .totalTests(totalTests)
                .totalStudents(totalStudents)
                .averageScore(averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0)
                .recentTests(recentTests)
                .build();
    }

    private TestSummary mapToTestSummary(Test test) {
        int studentCount = testSessionRepository.countByTestAndIsCompletedTrue(test);
        Double averageScore = testSessionRepository.findAverageScoreByTest(test);

        return TestSummary.builder()
                .id(test.getId())
                .title(test.getTitle())
                .studentCount(studentCount)
                .averageScore(averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0)
                .status(test.isActive() ? "ACTIVE" : "COMPLETED")
                .date(test.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM")))
                .build();
    }
}
