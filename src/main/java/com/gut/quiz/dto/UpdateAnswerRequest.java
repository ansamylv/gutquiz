package com.gut.quiz.dto;

import lombok.Data;

@Data
public class UpdateAnswerRequest {
    private Long id; // для существующих ответов
    private String text;
    private boolean correct;
}
