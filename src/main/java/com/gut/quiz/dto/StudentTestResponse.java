package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StudentTestResponse {
    private String testTitle;
    private String testDescription;
    private List<PublicQuestion> questions;
}