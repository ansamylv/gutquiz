-- Очистка таблиц
DELETE FROM test_sessions;
DELETE FROM tests;
DELETE FROM users;

-- Вставка пользователя
INSERT INTO users (code, first_name, last_name, middle_name) VALUES
('teacher123', 'Иван', 'Петров', 'Сергеевич');

-- Вставка тестов с новым полем is_published
INSERT INTO tests (title, description, teacher_id, created_at, is_active, is_published, public_link) VALUES
('Математика - тест 1', 'Базовые операции', 1, NOW(), true, true, 'math123'),
('Физика - контрольная', 'Механика', 1, NOW(), true, true, 'physics123'),
('История - экзамен', 'Древний мир', 1, NOW(), false, false, 'history123');

-- Вставка тестовых сессий
INSERT INTO test_sessions (test_id, student_first_name, student_last_name, student_group, started_at, completed_at, score, is_completed) VALUES
(1, 'Алексей', 'Сидоров', 'Группа 101', NOW(), NOW(), 85.5, true),
(1, 'Мария', 'Иванова', 'Группа 101', NOW(), NOW(), 92.0, true),
(2, 'Дмитрий', 'Козлов', 'Группа 102', NOW(), NOW(), 78.0, true),
(2, 'Анна', 'Петрова', 'Группа 102', NOW(), NOW(), 88.5, true);
