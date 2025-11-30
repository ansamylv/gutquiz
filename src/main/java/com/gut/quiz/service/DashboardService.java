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
import java.time.LocalDate;
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

        List<TestSummary> allTests = testRepository.findByTeacher(teacher).stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .map(this::mapToTestSummary)
                .toList();

        List<TestSummary> recentTests = allTests.stream()
                .limit(3)
                .toList();

        long draftTests = allTests.stream().filter(summary -> "DRAFT".equals(summary.getStatus())).count();
        long activeTests = allTests.stream().filter(summary -> "ACTIVE".equals(summary.getStatus())).count();
        long completedTests = allTests.stream().filter(summary -> "COMPLETED".equals(summary.getStatus())).count();

        return DashboardDTO.builder()
                .teacherName(teacher.getFirstName() + " " + teacher.getLastName())
                .totalTests(totalTests)
                .totalStudents(totalStudents)
                .averageScore(averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0)
                .activeTests((int) activeTests)
                .completedTests((int) completedTests)
                .draftTests((int) draftTests)
                .recentTests(recentTests)
                .allTests(allTests)
                .build();
    }

    private TestSummary mapToTestSummary(Test test) {
        int studentCount = testSessionRepository.countByTestAndIsCompletedTrue(test);
        Double averageScore = testSessionRepository.findAverageScoreByTest(test);
        boolean isDraft = !test.isPublished();
        boolean isActive = test.isPublished() && test.isActive();
        String status = isDraft ? "DRAFT" : (isActive ? "ACTIVE" : "COMPLETED");

        LocalDate createdDate = test.getCreatedAt() != null ? test.getCreatedAt().toLocalDate() : LocalDate.now();
        String dateLabel = createdDate.equals(LocalDate.now())
                ? "Сегодня"
                : createdDate.format(DateTimeFormatter.ofPattern("dd.MM"));

        return TestSummary.builder()
                .id(test.getId())
                .title(test.getTitle())
                .studentCount(studentCount)
                .averageScore(averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0)
                .status(status)
                .date(dateLabel)
                .active(isActive)
                .published(test.isPublished())
                .publicLink(test.getPublicLink())
                .build();
    }
}
