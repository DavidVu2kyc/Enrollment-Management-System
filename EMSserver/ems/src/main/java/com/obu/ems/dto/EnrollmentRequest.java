package com.obu.ems.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentRequest {
    private Long studentId; // Optional: for ADMIN enlisting on behalf
    @NotNull
    private Long sectionId;
}
