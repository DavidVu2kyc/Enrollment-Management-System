package com.obu.ems.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentResponse {
    private Long enrollmentId;
    private StudentResponse student;
    private SectionResponse section;
    private String status;
    private LocalDateTime enrolledAt;
}
