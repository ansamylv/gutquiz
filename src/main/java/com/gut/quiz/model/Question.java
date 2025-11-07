package com.gut.quiz.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    // ИНИЦИАЛИЗИРУЕМ список ответов и используем каскадное сохранение
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    // 💡 КРИТИЧНОЕ ИСПРАВЛЕНИЕ: МЕТОД, УСТАНАВЛИВАЮЩИЙ ОБРАТНУЮ ССЫЛКУ!
    public void addAnswer(Answer answer) {
        answers.add(answer);
        answer.setQuestion(this); // <-- ЭТО ТО, ЧТО ВЫЗЫВАЛО ОШИБКУ 500
    }

    public void removeAnswer(Answer answer) {
        answers.remove(answer);
        answer.setQuestion(null);
    }
}
