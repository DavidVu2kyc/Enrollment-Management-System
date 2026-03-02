package com.obu.ems.mapper;

import com.obu.ems.dto.StudentResponse;
import com.obu.ems.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentMapper {

    private final DegreeMapper degreeMapper;

    public StudentResponse mapToStudentResponse(Student student) {
        if (student == null) {
            return null;
        }
        return StudentResponse.builder()
                .studentId(student.getStudentId())
                .userId(student.getUser() != null ? student.getUser().getUserId() : null)
                .username(student.getUser() != null ? student.getUser().getUsername() : null)
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .studentNumber(student.getStudentNumber())
                .degree(degreeMapper.mapToDegreeResponse(student.getDegree()))
                .build();
    }
}
