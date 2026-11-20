INSERT INTO enclosures (name, allowed_animal_types, capacity)
SELECT v.name, v.allowed_animal_types, v.capacity
FROM (VALUES
    ('Вольер №1 для собак', 'Собака, Щенок', 3),
    ('Вольер №2 для кошек', 'Кошка, Котёнок', 4)
) AS v(name, allowed_animal_types, capacity)
WHERE NOT EXISTS (SELECT 1 FROM enclosures);

INSERT INTO animals (name, species, age, placement_status, enclosure_id)
SELECT v.name, v.species, v.age, v.placement_status, v.enclosure_id
FROM (VALUES
    ('Бобик', 'Собака', 2, 'IN_SHELTER', 1),
    ('Барсик', 'Кошка', 1, 'IN_SHELTER', 2),
    ('Рекс', 'Собака', 4, 'IN_SHELTER', CAST(NULL AS BIGINT))
) AS v(name, species, age, placement_status, enclosure_id)
WHERE NOT EXISTS (SELECT 1 FROM animals);

INSERT INTO users (name, email, phone, role, password, status, created_at)
SELECT v.name, v.email, v.phone, v.role, v.password, v.status, CURRENT_TIMESTAMP
FROM (VALUES
    ('Администратор', 'admin@shelter.ru', '+79990001122', 'ROLE_ADMIN', '$2a$10$GuI8MVg09JK.F.z5D/tPd.N8OnaG3fHppH3GiRzoncB1FBq98YEIG', 'ACTIVE'),
    ('Матвей', 'matvey@example.com', '+79991234567', 'ROLE_USER', '$2a$10$sPYaAxb/81SqNPoUbk.Y.e3Ddqt2gaf59J258oe4lqD10jYIi4KR.', 'ACTIVE'),
    ('Заблокированный Иван', 'blocked@example.com', '+79999999999', 'ROLE_USER', '$2a$10$c7Pvsr3piwZHHeXn9qcJKOvKyk6sPpGH8NWNkbPFt/kVHfOpXUp5O', 'BLOCKED')
) AS v(name, email, phone, role, password, status)
WHERE NOT EXISTS (SELECT 1 FROM users);

INSERT INTO adoption_applications (animal_id, applicant_id, status, created_at)
SELECT 1, 2, 'SUBMITTED', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM adoption_applications);

INSERT INTO animal_handover_records (application_id, preparation_date, confirmation_date, status)
SELECT 1, CURRENT_TIMESTAMP, CAST(NULL AS TIMESTAMP WITH TIME ZONE), 'PREPARED'
WHERE NOT EXISTS (SELECT 1 FROM animal_handover_records);
