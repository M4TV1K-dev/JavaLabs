-- Тестовые вольеры (id: 1, 2)
INSERT INTO enclosures (name, allowed_animal_types, capacity)
VALUES ('Вольер №1 для собак', 'Собака, Щенок', 3),
       ('Вольер №2 для кошек', 'Кошка, Котёнок', 4);

-- Тестовые животные (id: 1, 2, 3)
INSERT INTO animals (name, species, age, placement_status, enclosure_id)
VALUES ('Бобик', 'Собака', 2, 'IN_SHELTER', 1),
       ('Барсик', 'Кошка', 1, 'IN_SHELTER', 2),
       ('Рекс', 'Собака', 4, 'IN_SHELTER', NULL);

-- Тестовые пользователи с хэшированными паролями BCrypt:
-- 1) admin@shelter.ru / admin123 (ROLE_ADMIN, ACTIVE)
-- 2) matvey@example.com / user123 (ROLE_USER, ACTIVE)
-- 3) blocked@example.com / blocked123 (ROLE_USER, BLOCKED)
INSERT INTO users (name, email, phone, role, password, status, created_at)
VALUES ('Администратор', 'admin@shelter.ru', '+79990001122', 'ROLE_ADMIN', '$2a$10$GuI8MVg09JK.F.z5D/tPd.N8OnaG3fHppH3GiRzoncB1FBq98YEIG', 'ACTIVE', CURRENT_TIMESTAMP),
       ('Матвей', 'matvey@example.com', '+79991234567', 'ROLE_USER', '$2a$10$sPYaAxb/81SqNPoUbk.Y.e3Ddqt2gaf59J258oe4lqD10jYIi4KR.', 'ACTIVE', CURRENT_TIMESTAMP),
       ('Заблокированный Иван', 'blocked@example.com', '+79999999999', 'ROLE_USER', '$2a$10$c7Pvsr3piwZHHeXn9qcJKOvKyk6sPpGH8NWNkbPFt/kVHfOpXUp5O', 'BLOCKED', CURRENT_TIMESTAMP);

-- Тестовая заявка на усыновление (id: 1, на Бобика от Матвея)
INSERT INTO adoption_applications (animal_id, applicant_id, status, created_at)
VALUES (1, 2, 'SUBMITTED', CURRENT_TIMESTAMP);

-- Тестовый акт передачи (id: 1)
INSERT INTO animal_handover_records (application_id, preparation_date, confirmation_date, status)
VALUES (1, CURRENT_TIMESTAMP, NULL, 'PREPARED');
