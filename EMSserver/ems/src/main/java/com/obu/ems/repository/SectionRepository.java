package com.obu.ems.repository;

import com.obu.ems.model.Course;
import com.obu.ems.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByTerm_TermId(Long termId);
    List<Section> findByCourse_CourseIdAndTerm_TermId(Long courseId, Long termId);
    List<Section> findByCourse_CourseId(Long courseId);
    Section findBy_sectionId(Long sectionId);
    /**
     * Finds all sections a student is actively enrolled in for a given term,
     * used to check for schedule conflicts before enlisting.
     */
    @Query("""
            SELECT s FROM Section s
            JOIN Enrollment e ON e.section = s
            WHERE e.student.studentId = :studentId
              AND s.term.termId = :termId
              AND e.status IN ('PENDING', 'ENROLLED')
            """)
    List<Section> findEnrolledSectionsByStudentAndTerm(@Param("studentId") Long studentId,
                                                       @Param("termId") Long termId);
}
