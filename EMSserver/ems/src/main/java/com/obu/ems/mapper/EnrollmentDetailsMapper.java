package com.obu.ems.mapper;

import com.obu.ems.dto.EnrollmentResponse;
import com.obu.ems.model.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentDetailsMapper {
    private final StudentMapper studentMapper;
    private final SectionMapper sectionMapper;

    public EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

       return  EnrollmentResponse.builder()
               .enrollmentId(enrollment.getEnrollmentId())
               .student(studentMapper.mapToStudentResponse(enrollment.getStudent()))
               .section(sectionMapper.mapToSectionResponse(enrollment.getSection()))
               .status(enrollment.getStatus() !=    null ? enrollment.getStatus().name() : null)
               .enrolledAt(enrollment.getEnrolledAt())
               .build();

   }
}
