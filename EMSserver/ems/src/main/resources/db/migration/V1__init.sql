-- -- ================================================================
-- -- Orange and Bronze University — Enrollment Management System
-- -- V1__init.sql — Full Schema Creation
-- -- ================================================================

-- -- Users table (authentication)
-- CREATE TABLE users (
--     user_id     BIGSERIAL PRIMARY KEY,
--     username    VARCHAR(100) NOT NULL UNIQUE,
--     password_hash VARCHAR(255) NOT NULL,
--     role        VARCHAR(20)  NOT NULL CHECK (role IN ('STUDENT', 'ADMIN')),
--     created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
--     updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
-- );

-- -- Degrees offered by the university
-- CREATE TABLE degrees (
--     degree_id   BIGSERIAL PRIMARY KEY,
--     name        VARCHAR(150) NOT NULL UNIQUE,
--     description TEXT
-- );

-- -- Courses belonging to a degree
-- CREATE TABLE courses (
--     course_id   BIGSERIAL PRIMARY KEY,
--     code        VARCHAR(20)  NOT NULL UNIQUE,
--     title       VARCHAR(200) NOT NULL,
--     units       INTEGER      NOT NULL CHECK (units > 0),
--     degree_id   BIGINT       NOT NULL REFERENCES degrees(degree_id) ON DELETE CASCADE
-- );

-- -- Student profiles (linked to a user account)
-- CREATE TABLE students (
--     student_id      BIGSERIAL PRIMARY KEY,
--     user_id         BIGINT       NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
--     first_name      VARCHAR(100) NOT NULL,
--     last_name       VARCHAR(100) NOT NULL,
--     student_number  VARCHAR(20)  NOT NULL UNIQUE,
--     degree_id       BIGINT       NOT NULL REFERENCES degrees(degree_id)
-- );

-- -- Academic terms
-- CREATE TABLE terms (
--     term_id    BIGSERIAL PRIMARY KEY,
--     name       VARCHAR(100) NOT NULL,
--     year       INTEGER      NOT NULL CHECK (year > 2000),
--     semester   VARCHAR(20)  NOT NULL CHECK (semester IN ('FIRST', 'SECOND', 'SUMMER')),
--     is_active  BOOLEAN      NOT NULL DEFAULT FALSE,
--     UNIQUE (year, semester)
-- );

-- -- Physical rooms
-- CREATE TABLE rooms (
--     room_id     BIGSERIAL PRIMARY KEY,
--     building    VARCHAR(100) NOT NULL,
--     room_number VARCHAR(20)  NOT NULL,
--     capacity    INTEGER      NOT NULL CHECK (capacity > 0),
--     UNIQUE (building, room_number)
-- );

-- -- Weekly schedules (time slots)
-- CREATE TABLE schedules (
--     schedule_id  BIGSERIAL PRIMARY KEY,
--     day_of_week  VARCHAR(10) NOT NULL CHECK (day_of_week IN ('MON','TUE','WED','THU','FRI','SAT','SUN')),
--     start_time   TIME        NOT NULL,
--     end_time     TIME        NOT NULL,
--     CONSTRAINT valid_time_range CHECK (end_time > start_time)
-- );

-- -- Course sections opened each term
-- CREATE TABLE sections (
--     section_id      BIGSERIAL PRIMARY KEY,
--     section_code    VARCHAR(20)  NOT NULL,
--     course_id       BIGINT       NOT NULL REFERENCES courses(course_id),
--     term_id         BIGINT       NOT NULL REFERENCES terms(term_id),
--     room_id         BIGINT       REFERENCES rooms(room_id),
--     schedule_id     BIGINT       REFERENCES schedules(schedule_id),
--     faculty_name    VARCHAR(200),
--     max_seats       INTEGER      NOT NULL CHECK (max_seats > 0),
--     enrolled_count  INTEGER      NOT NULL DEFAULT 0 CHECK (enrolled_count >= 0),
--     CONSTRAINT enrolled_not_exceed_max CHECK (enrolled_count <= max_seats),
--     UNIQUE (term_id, section_code)
-- );

-- -- Student enrollment records
-- CREATE TABLE enrollments (
--     enrollment_id  BIGSERIAL PRIMARY KEY,
--     student_id     BIGINT      NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
--     section_id     BIGINT      NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
--     status         VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','ENROLLED','DROPPED','CANCELLED')),
--     enrolled_at    TIMESTAMP   NOT NULL DEFAULT NOW(),
--     UNIQUE (student_id, section_id)
-- );

-- -- ================================================================
-- -- Indexes for common query patterns
-- -- ================================================================
-- CREATE INDEX idx_enrollments_student ON enrollments(student_id);
-- CREATE INDEX idx_enrollments_section ON enrollments(section_id);
-- CREATE INDEX idx_sections_term       ON sections(term_id);
-- CREATE INDEX idx_sections_course     ON sections(course_id);
-- CREATE INDEX idx_courses_degree      ON courses(degree_id);
-- CREATE INDEX idx_students_user       ON students(user_id);
-- CREATE INDEX idx_students_degree     ON students(degree_id);

-- -- ================================================================
-- -- Default seed data — roles / terms / degree
-- -- ================================================================

-- -- Default ADMIN account (password: admin123 — bcrypt hashed)
-- INSERT INTO users (username, password_hash, role)
-- VALUES ('admin', '$2a$12$w.tz.lGjWKqBP0nKDxNF6eMTwxbLXiAdmh8c.P2VW.FaZdK0dJrWC', 'ADMIN');

-- -- Sample degree
-- INSERT INTO degrees (name, description)
-- VALUES
--     ('Bachelor of Science in Computer Science', 'Four-year CS degree covering algorithms, data structures, and software engineering.'),
--     ('Bachelor of Science in Information Technology', 'Four-year IT degree focusing on system administration and enterprise applications.');

-- -- Current active term
-- INSERT INTO terms (name, year, semester, is_active)
-- VALUES ('AY 2025-2026 First Semester', 2025, 'FIRST', TRUE);
