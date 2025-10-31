package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStats {
    private int totalTestsCreated;
    private int activeTestsNow;
    private int completedSessionsThisMonth;
    private int totalStudentsPassedTests;
    private double averageTestResult;
    private String teacherName;
}
