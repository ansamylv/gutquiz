-- 1. ОЧИСТКА: Удаляем в порядке, обратном ссылке (от ответов до пользователей)
DELETE FROM answers;
DELETE FROM questions;
DELETE FROM test_sessions;
DELETE FROM tests;
DELETE FROM users;

-- 2. ВСТАВКА: Пользователь (ID=1)
INSERT INTO users (id, code, first_name, last_name, middle_name) VALUES
(1, 'teacher123', 'Иван', 'Петров', 'Сергеевич');

-- 3. ВСТАВКА: Тесты (ссылаются на teacher_id = 1)
INSERT INTO tests (id, title, description, teacher_id, created_at, is_active, is_published, public_link) VALUES
(1, 'Математика - тест 1', 'Базовые операции', 1, NOW(), true, true, 'math123'),
(2, 'Физика - контрольная', 'Механика', 1, NOW(), true, true, 'physics123'),
(3, 'История - экзамен', 'Древний мир', 1, NOW(), false, false, 'history123');

-- 4. ВСТАВКА: Тестовые сессии (ссылаются на test_id 1 и 2)
INSERT INTO test_sessions (test_id, student_first_name, student_last_name, student_group, started_at, completed_at, score, is_completed) VALUES
(1, 'Алексей', 'Сидоров', 'Группа 101', NOW(), NOW(), 85.5, true),
(1, 'Мария', 'Иванова', 'Группа 101', NOW(), NOW(), 92.0, true),
(2, 'Дмитрий', 'Козлов', 'Группа 102', NOW(), NOW(), 78.0, true),
(2, 'Анна', 'Петрова', 'Группа 102', NOW(), NOW(), 88.5, true);