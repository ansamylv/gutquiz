package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TestStatsResponse {
    private String testTitle;
    private int totalStudents;
    private double averageScore;
    private int completedSessions;
    private int activeSessions;
    private List<StudentResult> studentResults;
}
