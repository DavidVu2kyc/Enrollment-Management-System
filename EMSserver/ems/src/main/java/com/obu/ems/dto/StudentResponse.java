package com.obu.ems.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResponse {
    private Long studentId;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String studentNumber;
    private DegreeResponse degree;
}
