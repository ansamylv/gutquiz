package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentAnswer {
    private Long id;
    private String text;
}
