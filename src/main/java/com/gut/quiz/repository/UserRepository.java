package com.gut.quiz.repository;

import com.gut.quiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository // Помечает как репозиторий Spring
public interface UserRepository extends JpaRepository<User, Long> {

    // Кастомный метод: поиск пользователя по коду (логину)
    Optional<User> findByCode(String code);
}