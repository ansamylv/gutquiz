package com.gut.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateQuestionRequest {
    private String text;
    private String type; // SINGLE, MULTIPLE, TEXT
    private List<UpdateAnswerRequest> answers;
}