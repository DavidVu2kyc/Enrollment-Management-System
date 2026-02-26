package com.obu.ems.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponse {
    private Long courseId;
    private String code;
    private String title;
    private Integer units;
    private Long degreeId;
}
