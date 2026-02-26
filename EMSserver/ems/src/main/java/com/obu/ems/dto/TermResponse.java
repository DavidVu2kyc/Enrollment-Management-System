package com.obu.ems.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TermResponse {
    private Long termId;
    private String name;
    private Integer year;
    private String semester;
    private Boolean isActive;
}
