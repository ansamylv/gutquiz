package com.gut.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateQuestion {
    private Long id;
    private String text;
    private String type;
    private List<UpdateAnswer> answers;
}
