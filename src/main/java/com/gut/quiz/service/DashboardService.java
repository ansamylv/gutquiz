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
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;
    private final UserRepository userRepository;

    public DashboardDTO getDashboard() {
        User teacher = userRepository.findAll().get(0);

        int totalTests = testRepository.countByTeacher(teacher);
        int totalStudents = testSessionRepository.countDistinctStudentsByTeacher(teacher);
        Double averageScore = testSessionRepository.findAverageScoreByTeacher(teacher);

        List<Test> tests = testRepository.findByTeacher(teacher);
        List<TestSummary> recentTests = tests.stream()
                .map(this::mapToTestSummary)
                .limit(3)
                .toList();

        return DashboardDTO.builder()
                .teacherName(teacher.getFirstName() + " " + teacher.getLastName())
                .totalTests(totalTests)
                .totalStudents(totalStudents)
                .averageScore(averageScore != null ? averageScore : 0.0)
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
                .averageScore(averageScore != null ? averageScore : 0.0)
                .status(test.isActive() ? "ACTIVE" : "COMPLETED")
                .date(test.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM")))
                .build();
    }
}