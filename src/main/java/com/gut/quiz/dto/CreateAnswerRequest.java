package com.gut.quiz.dto;

import lombok.Data;

@Data
public class CreateAnswerRequest {
    private String text;
    private boolean correct;
}