package com.gut.quiz.repository;

import com.gut.quiz.model.Test;
import com.gut.quiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository // Помечает как репозиторий Spring
public interface TestRepository extends JpaRepository<Test, Long> {

    int countByTeacher(User teacher); // Подсчет всех тестов преподавателя

    int countByTeacherAndIsActiveTrue(User teacher); // Подсчет активных тестов

    List<Test> findByTeacher(User teacher); // Найти все тесты преподавателя

    // Дополнительные полезные методы:

    // Найти опубликованный тест по публичной ссылке (для студента)
    Optional<Test> findByPublicLinkAndIsPublishedTrue(String publicLink);

}