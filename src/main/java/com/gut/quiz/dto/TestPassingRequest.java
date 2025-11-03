package com.gut.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class TestPassingRequest {
    private Long testId;
    private String studentFirstName;
    private String studentLastName;
    private String group;
    private List<QuestionAnswer> answers;
}

