package com.gut.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateTestRequest {
    private String title;
    private String description;
    private List<QuestionDto> questions;
}

