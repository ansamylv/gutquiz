package com.gut.quiz.dto;

import lombok.Data;

@Data
public class UpdateAnswer {
    private Long id;
    private String text;
    private boolean correct;
}
