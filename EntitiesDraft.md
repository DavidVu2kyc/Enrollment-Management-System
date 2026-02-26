Users 
    user_id PK
    username
    password_hash
    role
    created_at
    updated_at
Students
    student_id PK
    user_id FK
    first_name
    last_name
    student_number 
    degree_id FK
Degrees
    degree_id PK
    name 
    description
Course
    course_id PK
    code 
    title
    units
    degree_id FK
Terms
    term_id PK
    name 
    year
    semester
    is_active
rooms
    room_id PK
    building 
    room_number
    capacity
schedules
    schedule_id PK
    course_id FK
    term_id FK
    room_id FK
    instructor_id FK
    schedule_time
    schedule_day
sections
    section_id PK
    course_id FK
    term_id FK
    room_id FK 
    schedule_id FK
    max_seats
    enrolled_count 
    faculty_name
enrollments 
    enrollment_id PK
    student_id FK
    section_id FK
    status PENDING | ENROLLED | DROPPED | CANCELLED
    enrolled_at 

    
    

    