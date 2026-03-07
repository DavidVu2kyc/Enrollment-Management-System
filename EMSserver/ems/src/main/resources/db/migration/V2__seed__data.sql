-- ================================================================
-- Orange and Bronze University — Enrollment Management System
-- V2__seed_data.sql — 5 Records Per Table
-- ================================================================

-- ================================================================
-- LOGIN CREDENTIALS:
-- Admin: username = 'admin', password = 'admin123'
-- Students: all usernames listed below with password = 'password123'
--   - jsmith  (John Smith)
--   - mjones  (Mary Jones)
--   - rwilliams (Robert Williams)
--   - sbrown  (Sarah Brown)
--   - adavis  (Alice Davis)
-- ================================================================

-- ================================================================
-- Additional Users (keeping the admin from V1)
-- All student passwords are 'password123' (bcrypt hashed)
-- ================================================================
INSERT INTO users (username, password_hash, role) VALUES
    ('jsmith', '$2a$12$w.tz.lGjWKqBP0nKDxNF6eMTwxbLXiAdmh8c.P2VW.FaZdK0dJrWC', 'STUDENT'),
    ('mjones', '$2a$12$w.tz.lGjWKqBP0nKDxNF6eMTwxbLXiAdmh8c.P2VW.FaZdK0dJrWC', 'STUDENT'),
    ('rwilliams', '$2a$12$w.tz.lGjWKqBP0nKDxNF6eMTwxbLXiAdmh8c.P2VW.FaZdK0dJrWC', 'STUDENT'),
    ('sbrown', '$2a$12$w.tz.lGjWKqBP0nKDxNF6eMTwxbLXiAdmh8c.P2VW.FaZdK0dJrWC', 'STUDENT'),
    ('adavis', '$2a$12$w.tz.lGjWKqBP0nKDxNF6eMTwxbLXiAdmh8c.P2VW.FaZdK0dJrWC', 'STUDENT');

-- ================================================================
-- Additional Degrees (keeping the two from V1)
-- ================================================================
INSERT INTO degrees (name, description) VALUES
    ('Bachelor of Science in Computer Engineering', 'Five-year program combining computer science and electrical engineering.'),
    ('Bachelor of Science in Data Science', 'Four-year program focused on statistical analysis and machine learning.'),
    ('Bachelor of Science in Information Systems', 'Four-year program emphasizing business applications and systems analysis.');

-- Now we have 5 degrees total (2 from V1 + 3 new)

-- ================================================================
-- Additional Courses (5 per degree)
-- ================================================================
-- Courses for CS degree (degree_id = 1)
INSERT INTO courses (code, title, units, degree_id) VALUES
    ('CS101', 'Introduction to Programming', 3, 1),
    ('CS201', 'Data Structures and Algorithms', 4, 1),
    ('CS301', 'Database Management Systems', 3, 1),
    ('CS401', 'Software Engineering', 3, 1),
    ('CS450', 'Artificial Intelligence', 3, 1);

-- Courses for IT degree (degree_id = 2)
INSERT INTO courses (code, title, units, degree_id) VALUES
    ('IT101', 'Introduction to Information Technology', 3, 2),
    ('IT201', 'Networking Fundamentals', 4, 2),
    ('IT301', 'Web Development', 3, 2),
    ('IT401', 'System Administration', 3, 2),
    ('IT450', 'Cybersecurity Basics', 3, 2);

-- Courses for CompEng degree (degree_id = 3)
INSERT INTO courses (code, title, units, degree_id) VALUES
    ('CE101', 'Digital Logic Design', 4, 3),
    ('CE201', 'Microprocessors', 4, 3),
    ('CE301', 'Computer Architecture', 3, 3),
    ('CE401', 'Embedded Systems', 4, 3),
    ('CE450', 'Robotics', 3, 3);

-- Courses for Data Science degree (degree_id = 4)
INSERT INTO courses (code, title, units, degree_id) VALUES
    ('DS101', 'Introduction to Data Science', 3, 4),
    ('DS201', 'Statistical Methods', 4, 4),
    ('DS301', 'Machine Learning Fundamentals', 4, 4),
    ('DS401', 'Big Data Analytics', 3, 4),
    ('DS450', 'Data Visualization', 3, 4);

-- Courses for IS degree (degree_id = 5)
INSERT INTO courses (code, title, units, degree_id) VALUES
    ('IS101', 'Business Process Fundamentals', 3, 5),
    ('IS201', 'Systems Analysis and Design', 4, 5),
    ('IS301', 'Enterprise Architecture', 3, 5),
    ('IS401', 'IT Project Management', 3, 5),
    ('IS450', 'Business Intelligence', 3, 5);

-- ================================================================
-- Students (linked to the 5 student users created above)
-- ================================================================
INSERT INTO students (user_id, first_name, last_name, student_number, degree_id) VALUES
    (2, 'John', 'Smith', '2024-1001', 1),   -- CS student
    (3, 'Mary', 'Jones', '2024-1002', 2),   -- IT student
    (4, 'Robert', 'Williams', '2024-1003', 3), -- CompEng student
    (5, 'Sarah', 'Brown', '2024-1004', 4),  -- Data Science student
    (6, 'Alice', 'Davis', '2024-1005', 5);  -- IS student

-- ================================================================
-- Additional Terms
-- ================================================================
INSERT INTO terms (name, year, semester, is_active) VALUES
    ('AY 2024-2025 First Semester', 2024, 'FIRST', FALSE),
    ('AY 2024-2025 Second Semester', 2025, 'SECOND', FALSE),
    ('AY 2024-2025 Summer', 2025, 'SUMMER', FALSE),
    ('AY 2025-2026 Second Semester', 2026, 'SECOND', FALSE),
    ('AY 2025-2026 Summer', 2026, 'SUMMER', FALSE);

-- Note: V1 already created 'AY 2025-2026 First Semester' as active term

-- ================================================================
-- Rooms
-- ================================================================
INSERT INTO rooms (building, room_number, capacity) VALUES
    ('Engineering', '101', 50),
    ('Engineering', '102', 45),
    ('Science', '201', 60),
    ('Science', '202', 55),
    ('Liberal Arts', '301', 40);

-- ================================================================
-- Schedules
-- ================================================================
INSERT INTO schedules (day_of_week, start_time, end_time) VALUES
    ('MON', '08:00:00', '10:00:00'),
    ('TUE', '10:30:00', '12:30:00'),
    ('WED', '13:00:00', '15:00:00'),
    ('THU', '08:00:00', '10:00:00'),
    ('FRI', '15:30:00', '17:30:00');

-- ================================================================
-- Sections (5 sections for the active term)
-- ================================================================
-- Using active term from V1 (term_id = 1)
INSERT INTO sections (section_code, course_id, term_id, room_id, schedule_id, faculty_name, max_seats, enrolled_count) VALUES
    ('CS101-A', 1, 1, 1, 1, 'Dr. Johnson', 35, 0),   -- CS101 section
    ('IT101-A', 6, 1, 2, 2, 'Prof. Martinez', 30, 0), -- IT101 section
    ('CE101-A', 11, 1, 3, 3, 'Dr. Chen', 25, 0),     -- CE101 section
    ('DS101-A', 16, 1, 4, 4, 'Prof. Wilson', 30, 0), -- DS101 section
    ('IS101-A', 21, 1, 5, 5, 'Dr. Taylor', 40, 0);   -- IS101 section

-- ================================================================
-- Enrollments (mix of PENDING and ENROLLED)
-- ================================================================
INSERT INTO enrollments (student_id, section_id, status, enrolled_at) VALUES
    -- Student 1 (John Smith) enrollments
    (1, 1, 'ENROLLED', NOW() - INTERVAL '5 days'),
    (1, 2, 'ENROLLED', NOW() - INTERVAL '5 days'),
    (1, 3, 'PENDING', NOW() - INTERVAL '2 days'),
    
    -- Student 2 (Mary Jones) enrollments
    (2, 2, 'ENROLLED', NOW() - INTERVAL '4 days'),
    (2, 4, 'PENDING', NOW() - INTERVAL '1 day'),
    
    -- Student 3 (Robert Williams) enrollments
    (3, 3, 'ENROLLED', NOW() - INTERVAL '3 days'),
    (3, 5, 'ENROLLED', NOW() - INTERVAL '3 days'),
    
    -- Student 4 (Sarah Brown) enrollments
    (4, 4, 'ENROLLED', NOW() - INTERVAL '2 days'),
    (4, 1, 'DROPPED', NOW() - INTERVAL '10 days'),
    
    -- Student 5 (Alice Davis) enrollments
    (5, 5, 'ENROLLED', NOW() - INTERVAL '1 day'),
    (5, 2, 'PENDING', NOW());

-- Update enrolled_count in sections based on ENROLLED status
UPDATE sections 
SET enrolled_count = (
    SELECT COUNT(*) 
    FROM enrollments 
    WHERE enrollments.section_id = sections.section_id 
    AND status = 'ENROLLED'
);

-- ================================================================
-- Verification Queries (commented out - for manual checking)
-- ================================================================
/*
-- Check record counts
SELECT 'users' as table_name, COUNT(*) FROM users UNION ALL
SELECT 'degrees', COUNT(*) FROM degrees UNION ALL
SELECT 'courses', COUNT(*) FROM courses UNION ALL
SELECT 'students', COUNT(*) FROM students UNION ALL
SELECT 'terms', COUNT(*) FROM terms UNION ALL
SELECT 'rooms', COUNT(*) FROM rooms UNION ALL
SELECT 'schedules', COUNT(*) FROM schedules UNION ALL
SELECT 'sections', COUNT(*) FROM sections UNION ALL
SELECT 'enrollments', COUNT(*) FROM enrollments;

-- Show enrollments with details
SELECT 
    s.first_name || ' ' || s.last_name as student_name,
    c.code as course_code,
    e.status,
    e.enrolled_at
FROM enrollments e
JOIN students s ON e.student_id = s.student_id
JOIN sections sec ON e.section_id = sec.section_id
JOIN courses c ON sec.course_id = c.course_id
ORDER BY e.enrolled_at DESC;

-- Show all users with their roles
SELECT username, role FROM users;
*/


