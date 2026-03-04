package com.obu.ems.mapper;

import com.obu.ems.dto.CourseResponse;
import com.obu.ems.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    public CourseResponse mapToCourseResponse(Course course) {
        if (course == null) {
            return null;
        }
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .code(course.getCode())
                .title(course.getTitle())
                .units(course.getUnits())
                .degreeId(course.getDegree() != null ? course.getDegree().getDegreeId() : null)
                .build();
    }
}
