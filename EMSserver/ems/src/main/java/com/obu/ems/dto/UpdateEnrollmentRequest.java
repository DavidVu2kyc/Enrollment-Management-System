package com.obu.ems.dto;

import com.obu.ems.model.Enrollment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateEnrollmentRequest {
    private Long sectionId;
    private Enrollment.Status status;
}
