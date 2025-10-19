package com.gut.quiz.service;

import com.gut.quiz.dto.AuthorizationRequest;
import com.gut.quiz.dto.AuthorizationResponse;
import com.gut.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;

    public AuthorizationResponse authorize(AuthorizationRequest authorizationRequest) {

    }
}
