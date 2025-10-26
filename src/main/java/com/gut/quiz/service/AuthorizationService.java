package com.gut.quiz.service;

import com.gut.quiz.dto.AuthorizationRequest;
import com.gut.quiz.dto.AuthorizationResponse;
import com.gut.quiz.model.User;
import com.gut.quiz.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final UserRepository userRepository;

    public AuthorizationResponse authorize(AuthorizationRequest authorizationRequest) {
        String code = authorizationRequest.getCode();

        Optional<User> userOptional = userRepository.findByCode(code);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return AuthorizationResponse.builder()
                    .success(true)
                    .message("Вход выполнен успешно")
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .middleName(user.getMiddleName())
                    .build();
        } else {
            return AuthorizationResponse.builder()
                    .success(false)
                    .message("Неверный код")
                    .build();
        }
    }

    public AuthorizationResponse registerTeacher(AuthorizationRequest authorizationRequest) {
        String code = authorizationRequest.getCode();

        if (userRepository.findByCode(code).isPresent()) {
            return AuthorizationResponse.builder()
                    .success(false)
                    .message("Этот код уже занят")
                    .build();
        }

        User newUser = new User();
        newUser.setCode(code);
        newUser.setFirstName(authorizationRequest.getFirstName());
        newUser.setLastName(authorizationRequest.getLastName());
        newUser.setMiddleName(authorizationRequest.getMiddleName());

        userRepository.save(newUser);

        return AuthorizationResponse.builder()
                .success(true)
                .message("Регистрация успешна")
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .middleName(newUser.getMiddleName())
                .build();
    }
}
