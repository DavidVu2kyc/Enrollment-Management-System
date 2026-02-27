package com.obu.ems.repository;

import com.obu.ems.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByDegree_DegreeId(Long degreeId);
}
