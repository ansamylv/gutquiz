package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicAnswer {
    private Long id;
    private String text;
    // НЕ включаем isCorrect!
}
