package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class StudentResult {
    private String studentName;
    private String group;
    private double score;
    private LocalDateTime completedAt;
}
