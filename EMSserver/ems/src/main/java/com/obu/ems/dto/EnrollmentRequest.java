package com.obu.ems.dto;

import com.obu.ems.model.Enrollment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class EnrollmentRequest {
    private Long studentId; // Optional: for ADMIN enlisting on behalf
    private Long sectionId;
    private List<Long> sectionIds; // For bulk enrollment
    private Enrollment.Status status = Enrollment.Status.PENDING;

}
