package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuestionWithAnswers {
    private String questionText;
    private String studentAnswer;
    private String correctAnswer;
    private boolean isCorrect;
}
