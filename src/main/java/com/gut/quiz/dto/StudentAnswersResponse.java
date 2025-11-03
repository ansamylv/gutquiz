package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StudentAnswersResponse {
    private String studentName;
    private String group;
    private double score;
    private LocalDateTime completedAt;
    private List<QuestionWithAnswers> answers;
}