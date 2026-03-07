package com.obu.ems.service;

import com.obu.ems.dto.StudentResponse;
import com.obu.ems.dto.UpdateStudentRequest;
import com.obu.ems.mapper.DegreeMapper;
import com.obu.ems.model.Degree;
import com.obu.ems.model.Student;
import com.obu.ems.repository.DegreeRepository;
import com.obu.ems.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {


//    List all students (ADMIN only)
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private DegreeRepository degreeRepository;

    private final DegreeMapper degreeMapper;

//    Get all students ( a list of students - admin role only )
        public Page<StudentResponse> getAll(Long degreeId, Pageable pageable)
    {
        Page<Student> studentList = studentRepository.findByDegree_DegreeId(degreeId,pageable);
//        CONVERT student to Student Response
        return studentList.map(this::mapToStudentResponse);
    }

    public StudentResponse mapToStudentResponse( Student student )
    {
        return StudentResponse.builder()
                .studentId(student.getStudentId())
                .userId(student.getUser().getUserId())
                .username(student.getUser().getUsername())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .studentNumber(student.getStudentNumber())
                .degree(degreeMapper.mapToDegreeResponse(student.getDegree()))
                .build();
    }

    @Transactional(readOnly = true)
    //    Get student by user ID (for auth flow)
    public StudentResponse getByUserId(Long userId)
    {
        Student student = studentRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return mapToStudentResponse(student);

    }

    @Transactional(readOnly = true)
    //    update student profile
    public StudentResponse updateStudentRequest(Long useIid, UpdateStudentRequest request)
    {
        // fetch student and verify  existence of that student
        Student student = studentRepository.findByUser_UserId(useIid).orElseThrow(() -> new RuntimeException("Student not found")) ;

        // Update basic info
        if(request.getFirstName() != null)
        {
            student.setFirstName(request.getFirstName());
        }

        if(request.getLastName() != null)
        {
            student.setLastName(request.getLastName());
        }

        // update degree by ADMIN only

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin  = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if(isAdmin && request.getDegreeId()!=null)
        {
            Degree degree = degreeRepository.findById(request.getDegreeId())
                    .orElseThrow(() -> new RuntimeException("Degree not found"));
            student.setDegree(degree);
        }

        // save updated student
        Student savedStudent = studentRepository.save(student);

        // return mapStudentResponse
        return mapToStudentResponse(savedStudent);

    }

}
