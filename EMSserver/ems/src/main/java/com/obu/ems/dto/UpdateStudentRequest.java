package com.obu.ems.dto;

import lombok.Data;

@Data
public class UpdateStudentRequest {
    private String firstName;
    private String lastName;
    private Long degreeId;
}
