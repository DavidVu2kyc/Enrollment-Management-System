package com.obu.ems.repository;

import com.obu.ems.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser_UserId(Long userId);
    boolean existsByStudentNumber(String studentNumber);
    Page<Student> findByDegree_DegreeId(Long degreeId, Pageable pageable);
}
