package com.gut.quiz.repository;

import com.gut.quiz.model.Test;
import com.gut.quiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    int countByTeacher(User teacher);
    int countByTeacherAndIsActiveTrue(User teacher);
}
