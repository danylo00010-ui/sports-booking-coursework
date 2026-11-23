DROP DATABASE IF EXISTS sports_booking;
CREATE DATABASE sports_booking
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sports_booking;

CREATE TABLE sport_facility (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                code VARCHAR(40) NOT NULL,
                                name VARCHAR(160) NOT NULL,
                                sport_type VARCHAR(100) NOT NULL,
                                max_capacity INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE app_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          full_name VARCHAR(160) NOT NULL,
                          email VARCHAR(120) NOT NULL,
                          phone VARCHAR(40) NOT NULL,
                          category VARCHAR(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE time_slot (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           facility_id BIGINT NOT NULL,
                           slot_date VARCHAR(10) NOT NULL,
                           start_time VARCHAR(5) NOT NULL,
                           end_time VARCHAR(5) NOT NULL,
                           duration_minutes INT NOT NULL,
                           CONSTRAINT fk_slot_facility FOREIGN KEY (facility_id) REFERENCES sport_facility(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE booking (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         time_slot_id BIGINT NOT NULL,
                         state VARCHAR(30) NOT NULL,
                         participants_count INT NOT NULL,
                         equipment_score DOUBLE NOT NULL,
                         f1 DOUBLE NOT NULL,
                         f2 DOUBLE NOT NULL,
                         f3 DOUBLE NOT NULL,
                         priority_index DOUBLE NOT NULL,
                         CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                         CONSTRAINT fk_booking_slot FOREIGN KEY (time_slot_id) REFERENCES time_slot(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO sport_facility (code, name, sport_type, max_capacity) VALUES
                                                                      ('SF-01', 'Великий ігровий зал №1', 'Баскетбол / Волейбол', 30),
                                                                      ('SF-02', 'Критий тенісний корт', 'Великий теніс', 4),
                                                                      ('SF-03', 'Плавальний басейн «Олімп»', 'Плавання', 24);

INSERT INTO app_user (full_name, email, phone, category) VALUES
                                                             ('Махньов Данило Вячеславович', 'makhnov.d@nupp.edu.ua', '+380501234567', 'Студент'),
                                                             ('Коваленко Олена Сергіївна', 'kovalenko.o@nupp.edu.ua', '+380679876543', 'Тренер');

INSERT INTO time_slot (facility_id, slot_date, start_time, end_time, duration_minutes) VALUES
                                                                                           (1, '2026-11-25', '10:00', '12:00', 120),
                                                                                           (2, '2026-11-25', '14:00', '15:30', 90);

INSERT INTO booking (user_id, time_slot_id, state, participants_count, equipment_score, f1, f2, f3, priority_index) VALUES
    (1, 1, 'CREATED', 15, 60.0, 50.0, 50.0, 60.0, 52.5);