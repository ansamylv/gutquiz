package com.gut.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateQuestionRequest {
    private String text;
    private String type; // SINGLE, MULTIPLE, TEXT
    private List<CreateAnswerRequest> answers;
}