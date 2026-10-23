-- Таблица вольеров
CREATE TABLE IF NOT EXISTS enclosures (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    allowed_animal_types VARCHAR(500) NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0)
);

-- Таблица животных
CREATE TABLE IF NOT EXISTS animals (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    species VARCHAR(100) NOT NULL,
    age INT NOT NULL CHECK (age >= 0),
    placement_status VARCHAR(50) NOT NULL,
    enclosure_id BIGINT REFERENCES enclosures(id) ON DELETE SET NULL
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Таблица заявок на усыновление
CREATE TABLE IF NOT EXISTS adoption_applications (
    id BIGSERIAL PRIMARY KEY,
    animal_id BIGINT NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    applicant_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Таблица актов передачи
CREATE TABLE IF NOT EXISTS animal_handover_records (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES adoption_applications(id) ON DELETE CASCADE,
    preparation_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    confirmation_date TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) NOT NULL
);
