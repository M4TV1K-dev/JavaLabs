INSERT INTO enclosures (name, allowed_animal_types, capacity)
VALUES ('Вольер №1 для собак', 'Собака, Щенок', 3),
       ('Вольер №2 для кошек', 'Кошка, Котёнок', 4);

INSERT INTO animals (name, species, age, placement_status, enclosure_id)
VALUES ('Бобик', 'Собака', 2, 'IN_SHELTER', 1),
       ('Барсик', 'Кошка', 1, 'IN_SHELTER', 2),
       ('Рекс', 'Собака', 4, 'IN_SHELTER', NULL);

INSERT INTO users (name, email, phone, role, created_at)
VALUES ('Матвей', 'matvey@example.com', '+79991234567', 'ADOPTER', CURRENT_TIMESTAMP),
       ('Анна', 'anna@example.com', '+79997654321', 'VOLUNTEER', CURRENT_TIMESTAMP);

INSERT INTO adoption_applications (animal_id, applicant_id, status, created_at)
VALUES (1, 1, 'SUBMITTED', CURRENT_TIMESTAMP);

INSERT INTO animal_handover_records (application_id, preparation_date, confirmation_date, status)
VALUES (1, CURRENT_TIMESTAMP, NULL, 'PREPARED');
