package com.gut.quiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateTestRequest {
    private String title;
    private String description;
    private List<UpdateQuestion> questions;
}

