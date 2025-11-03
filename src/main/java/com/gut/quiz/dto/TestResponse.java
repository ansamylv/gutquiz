package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TestResponse {
    private Long id;
    private String title;
    private String description;
    private String publicLink; // ← ДОБАВИТЬ
    private boolean isPublished; // ← ДОБАВИТЬ
    private List<QuestionDto> questions;
}
