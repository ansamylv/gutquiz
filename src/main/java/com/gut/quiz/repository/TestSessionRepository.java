package com.gut.quiz.repository;

import com.gut.quiz.model.TestSession;
import com.gut.quiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {

    @Query("SELECT COUNT(ts) FROM TestSession ts WHERE ts.test.teacher = :teacher AND ts.isCompleted = true AND ts.completedAt >= :startOfMonth")
    int countCompletedSessionsThisMonth(@Param("teacher") User teacher, @Param("startOfMonth") LocalDateTime startOfMonth);

    @Query("SELECT COUNT(DISTINCT CONCAT(ts.studentFirstName, ts.studentLastName, ts.studentGroup)) FROM TestSession ts WHERE ts.test.teacher = :teacher AND ts.isCompleted = true")
    int countDistinctStudentsByTeacher(@Param("teacher") User teacher);

    @Query("SELECT AVG(ts.score) FROM TestSession ts WHERE ts.test.teacher = :teacher AND ts.isCompleted = true")
    Double findAverageScoreByTeacher(@Param("teacher") User teacher);
}
