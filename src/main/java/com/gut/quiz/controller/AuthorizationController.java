package com.gut.quiz.controller;

import com.gut.quiz.dto.AuthorizationRequest;
import com.gut.quiz.dto.AuthorizationResponse;
import com.gut.quiz.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping
    public AuthorizationResponse authorizationUser(AuthorizationRequest authorizationRequest) {
        return authorizationService.authorize(authorizationRequest);
    }
}
