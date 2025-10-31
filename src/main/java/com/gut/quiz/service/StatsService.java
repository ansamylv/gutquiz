package com.gut.quiz.service;

import com.gut.quiz.dto.UserStats;
import com.gut.quiz.model.User;
import com.gut.quiz.repository.TestRepository;
import com.gut.quiz.repository.TestSessionRepository;
import com.gut.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;
    private final UserRepository userRepository;

    public UserStats getCurrentUserStats(String currentUserCode) {
        User teacher = userRepository.findByCode(currentUserCode)
                .orElseThrow(() -> new RuntimeException("Преподаватель не найден"));

        int totalTests = testRepository.countByTeacher(teacher);
        int activeTests = testRepository.countByTeacherAndIsActiveTrue(teacher);

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        int completedSessions = testSessionRepository.countCompletedSessionsThisMonth(teacher, startOfMonth);

        int totalStudents = testSessionRepository.countDistinctStudentsByTeacher(teacher);

        Double averageScore = testSessionRepository.findAverageScoreByTeacher(teacher);

        return UserStats.builder()
                .totalTestsCreated(totalTests)
                .activeTestsNow(activeTests)
                .completedSessionsThisMonth(completedSessions)
                .totalStudentsPassedTests(totalStudents)
                .averageTestResult(averageScore != null ? averageScore : 0.0)
                .teacherName(teacher.getFirstName() + " " + teacher.getLastName())
                .build();
    }
}
