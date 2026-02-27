package com.obu.ems.repository;

import com.obu.ems.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser_UserId(Long userId);
    boolean existsByStudentfirstName( String firstName);
    boolean  existsByStudentlastName( String lastName);
    boolean existsByStudentNumber(String studentNumber);
    Page<Student> findByDegree_DegreeId(Long degreeId, Pageable pageable);

}
