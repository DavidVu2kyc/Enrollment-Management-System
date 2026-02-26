package com.obu.ems.repository;

import com.obu.ems.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByDegree_DegreeId(Long degreeId);
}
