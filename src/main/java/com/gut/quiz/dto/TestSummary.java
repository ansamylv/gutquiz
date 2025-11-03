package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestSummary {
    private Long id;
    private String title;
    private int studentCount;
    private double averageScore;
    private String status;
    private String date;
}
