package com.obu.ems.dto;

import com.obu.ems.model.Enrollment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class EnrollmentResponse {
    private Long enrollmentId;
    private StudentResponse student;
    private SectionResponse section;
    private String status;
    private LocalDateTime enrolledAt;
private String message;

    public EnrollmentResponse(String message) {
        this.message = message;
    }

}
