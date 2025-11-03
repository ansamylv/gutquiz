package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuestionDto {
    private Long id;
    private String text;
    private String type;
    private List<AnswerDto> answers;
}
