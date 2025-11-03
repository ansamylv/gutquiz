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
    private List<TestSummary> recentTests;
}
