INSERT INTO users (id, code, first_name, last_name, middle_name) VALUES
(1, 'teacher123', 'Иван', 'Петров', 'Сергеевич');

INSERT INTO tests (id, title, description, teacher_id, created_at, is_active) VALUES
(1, 'Математика - тест 1', 'Базовые операции', 1, NOW(), true),
(2, 'Физика - контрольная', 'Механика', 1, NOW(), true),
(3, 'История - экзамен', 'Древний мир', 1, NOW(), false);

INSERT INTO test_sessions (id, test_id, student_first_name, student_last_name, student_group, started_at, completed_at, score, is_completed) VALUES
(1, 1, 'Алексей', 'Сидоров', 'Группа 101', NOW(), NOW(), 85.5, true),
(2, 1, 'Мария', 'Иванова', 'Группа 101', NOW(), NOW(), 92.0, true),
(3, 2, 'Дмитрий', 'Козлов', 'Группа 102', NOW(), NOW(), 78.0, true),
(4, 2, 'Анна', 'Петрова', 'Группа 102', NOW(), NOW(), 88.5, true);
