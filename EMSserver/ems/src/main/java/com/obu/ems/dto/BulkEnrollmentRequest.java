package com.obu.ems.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkEnrollmentRequest {

    private Long studentId;
    private List<Long> sectionIds;
}
