package com.obu.ems.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DegreeResponse {
    private Long degreeId;
    private String name;
    private String description;
}
