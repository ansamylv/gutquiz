package com.gut.quiz.repository;

import com.gut.quiz.model.Test;
import com.gut.quiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    int countByTeacher(User teacher);
    int countByTeacherAndIsActiveTrue(User teacher);
    List<Test> findByTeacher(User teacher);

    // Дополнительные полезные методы:
    Optional<Test> findByPublicLinkAndIsPublishedTrue(String publicLink);

}
