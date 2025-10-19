package com.gut.quiz.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuthorizationRequest {

    private String code;

    private String firstName;

    private String lastName;

    private String middleName;
}
