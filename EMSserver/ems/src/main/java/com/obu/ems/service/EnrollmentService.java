package com.obu.ems.service;

import com.obu.ems.dto.EnrollmentRequest;
import com.obu.ems.dto.EnrollmentResponse;
import com.obu.ems.dto.UpdateEnrollmentRequest;
import com.obu.ems.exception.ResourceNotFoundException;
import com.obu.ems.mapper.EnrollmentMapper;
import com.obu.ems.model.Enrollment;
import com.obu.ems.model.Section;
import com.obu.ems.model.Student;
import com.obu.ems.model.User;
import com.obu.ems.repository.SectionRepository;
import com.obu.ems.repository.StudentRepository;
import com.obu.ems.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.obu.ems.repository.EnrollmentRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service    
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentMapper enrollmentMapper;


    //   enlist a student in a section
    @Transactional
    public EnrollmentResponse enlistStudent (Long studentId, EnrollmentRequest enrollmentRequest) {

       //   validate and fetch student
       if(!studentId.equals(enrollmentRequest.getStudentId())) {
           throw new IllegalArgumentException("Student ID in path does not match request body.");
       }

       Optional<Enrollment> enrollment = enrollmentRepository.findByStudent_StudentIdAndSection_SectionId(studentId,enrollmentRequest.getSectionId());

        //  validate and fetch student
        Student student =  studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        //  validate and fetch section
        Section section = sectionRepository.findBy_sectionId(enrollmentRequest.getSectionId());

        return  enrollmentMapper.mapToEnrollmentResponse(enrollment.orElse(null));

    }

    //   get the current student's enrollment list ( EAF)
    @Transactional
        public List<EnrollmentResponse> getMyEnrollments(Long termId , EnrollmentRequest enrollmentRequest ) {
        List<Enrollment> enrollments = enrollmentRepository
                .findByStudent_StudentIdAndSection_Term_TermId(enrollmentRequest.getStudentId(), enrollmentRequest.getSectionId());

        if(enrollments.isEmpty()) {
            throw new ResourceNotFoundException("Can not find enrollment list ");
        }

        return enrollments.stream()
                .map(enrollmentMapper::mapToEnrollmentResponse)
                .toList();

    }

    //   Get enrollment detail
    public EnrollmentResponse getById(Long enrollmentId) {
        return null;
    }
    //  update enrollment status  - ADMIN ROLE only
    public EnrollmentResponse updateStatus(Long enrollmentId, EnrollmentRequest enrollmentRequest, UpdateEnrollmentRequest  updateEnrollmentRequest) {
        //  enrollment verification exists
                Enrollment enroll = enrollmentRepository
                        .findByStudent_StudentIdAndSection_SectionId(enrollmentRequest.getStudentId(),enrollmentRequest.getSectionId())
                        .orElseThrow(()->new RuntimeException("Enrollment not found for the given student and section."));
        // Role check : Admin only
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                Boolean isAdmin  = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if(!isAdmin) {
                    throw new AccessDeniedException("Only administrators can update enrollment statuses.");
                }

        //  update enrollment status ( Pending -> Enrolled , enrolled ->dropped )
            enroll.setStatus(updateEnrollmentRequest.getStatus());
        //  return mapper class
        return enrollmentMapper.mapToEnrollmentResponse(enroll);
        }

    //   drop enrollment -( student or ADMIN )
    public EnrollmentResponse drop(Long studentId,Boolean isAdmin,EnrollmentRequest enrollmentRequest ) {

        //   verify enrollment exists belonging to that student  ( enrollmentId, studentId )
        Enrollment enroll = enrollmentRepository.findByStudent_StudentIdAndSection_SectionId(enrollmentRequest.getStudentId(),enrollmentRequest.getSectionId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found for the given student and section."));

        //  Students can only drop their own enrollments
        if (!Boolean.TRUE.equals(isAdmin) && !studentId.equals(enrollmentRequest.getStudentId())) {
            throw new SecurityException("Students can only drop their own enrollments.");
        }

        //  Route logic based on Admin role
        if(Boolean.TRUE.equals(isAdmin)) {
        //  hard delete
            enrollmentRepository.delete(enroll);
            return new EnrollmentResponse("Enrollment permanently removed (Hard Delete).");
        }
        else
        {
        //  soft delete -fixed  access
            enroll.setStatus(Enrollment.Status.DROPPED);
            enrollmentRepository.save(enroll);
            return new EnrollmentResponse("Soft delete successfully");
        }

    }
}
