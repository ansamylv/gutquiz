package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class AnswerResponse {
    private String text;
    private boolean correct;
}
