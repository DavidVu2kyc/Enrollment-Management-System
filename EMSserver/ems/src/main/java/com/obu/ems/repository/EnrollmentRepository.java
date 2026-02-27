package com.obu.ems.repository;

import com.obu.ems.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent_StudentId(Long studentId);

    List<Enrollment> findBySection_SectionId(Long sectionId);

    List<Enrollment> findByStudent_StudentIdAndSection_Term_TermId(Long studentId, Long termId);

    List<Enrollment> findByStudent_StudentIdAndStatusAndSection_Term_TermId(
            Long studentId, Enrollment.Status status, Long termId);

    Optional<Enrollment> findByStudent_StudentIdAndSection_SectionId(Long studentId, Long sectionId);

    boolean existsByStudent_StudentIdAndSection_SectionId(Long studentId, Long sectionId);
}
