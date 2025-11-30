package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardDTO {
    private String teacherName;
    private int totalTests;
    private int totalStudents;
    private double averageScore;
    private int activeTests;
    private int completedTests;
    private int draftTests;
    private List<TestSummary> recentTests;
    private List<TestSummary> allTests;
}
