package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestPassingResponse {

    private double score;

    // Имена полей, которые мы используем в билдере
    private int correctAnswersCount;
    private int totalQuestionsCount;

    private String message;
}
