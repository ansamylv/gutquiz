package com.gut.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionAnswer {
    private Long questionId;
    private List<Long> selectedAnswerIds;
    private String textAnswer;
}
