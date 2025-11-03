package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestPassingResponse {
    private double score;
    private int correctAnswers;
    private int totalQuestions;
    private String resultMessage;
}
