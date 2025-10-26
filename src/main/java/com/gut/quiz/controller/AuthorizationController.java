package com.gut.quiz.controller;

import com.gut.quiz.dto.AuthorizationRequest;
import com.gut.quiz.dto.AuthorizationResponse;
import com.gut.quiz.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public AuthorizationResponse loginUser(@RequestBody AuthorizationRequest authorizationRequest) {
        return authorizationService.authorize(authorizationRequest);
    }

    @PostMapping("/register/teacher")
    public AuthorizationResponse registerTeacher(@RequestBody AuthorizationRequest authorizationRequest) {
        return authorizationService.registerTeacher(authorizationRequest);
    }
}
