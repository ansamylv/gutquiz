package com.gut.quiz.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "test_sessions")
public class TestSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    private String studentFirstName;
    private String studentLastName;
    private String studentGroup;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Double score;
    private boolean isCompleted;

    // ЯВНЫЙ ГЕТТЕР для boolean поля
    public boolean getIsCompleted() {
        return isCompleted;
    }

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        isCompleted = false;
    }
}
