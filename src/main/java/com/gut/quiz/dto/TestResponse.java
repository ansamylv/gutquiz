package com.gut.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResponse {
    private Long id;
    private String title;
    private String description;
    private String publicLink;
    private boolean isPublished;
    private boolean isActive;
    private List<QuestionDto> questions;
}
