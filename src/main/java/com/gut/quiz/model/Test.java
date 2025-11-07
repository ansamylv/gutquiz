package com.gut.quiz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "tests")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    // ДОБАВЛЯЕМ связь с вопросами
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    private String publicLink;
    private boolean isPublished;

    private LocalDateTime createdAt;
    private boolean isActive;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;

        if (this.publicLink == null) {
            // 💡 ИСПРАВЛЕНИЕ: Транслитерация и удаление всех небезопасных символов
            String tempSlug = this.title.toLowerCase();

            // 1. Убираем ударения и спецсимволы (Normalizer.Form.NFD)
            tempSlug = Normalizer.normalize(tempSlug, Normalizer.Form.NFD);

            // 2. Оставляем только латинские буквы и цифры (Удаляем кириллицу)
            String baseSlug = tempSlug
                    .replaceAll("[^a-z0-9]", "-") // Оставляем только a-z и 0-9
                    .replaceAll("-+", "-")
                    .replaceAll("^-|-$", ""); // Удаляем начальные/конечные дефисы

            String randomId = UUID.randomUUID().toString().substring(0, 8);
            this.publicLink = baseSlug + "-" + randomId;
        }

        // По умолчанию тест не опубликован
        if (!this.isPublished) {
            this.isPublished = false;
        }
    }

    // ДОБАВЛЯЕМ вспомогательные методы для работы с вопросами
    public void addQuestion(Question question) {
        questions.add(question);
        question.setTest(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTest(null);
    }
}