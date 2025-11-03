package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PublicQuestion {
    private Long id;
    private String text;
    private String type; // SINGLE, MULTIPLE, TEXT
    private List<PublicAnswer> answers;
}
