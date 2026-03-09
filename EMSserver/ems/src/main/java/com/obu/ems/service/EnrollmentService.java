package com.obu.ems.service;

import com.obu.ems.dto.EnrollmentRequest;
import com.obu.ems.dto.EnrollmentResponse;
import com.obu.ems.dto.UpdateEnrollmentRequest;
import org.springframework.security.access.AccessDeniedException;
import com.obu.ems.exception.BadRequestException;
import com.obu.ems.exception.ConflictException;
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

    // enlist a student in a section
    @Transactional
    public EnrollmentResponse enlistStudent(Long studentId, EnrollmentRequest enrollmentRequest) {

        // validate and fetch student
        if (!studentId.equals(enrollmentRequest.getStudentId())) {
            throw new BadRequestException("Student ID in path does not match request body.");
        }

        // validate and fetch student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found."));

        // validate and fetch section
        Section section = sectionRepository.findById(enrollmentRequest.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found."));

        // Check if already enrolled
        Optional<Enrollment> existingEnrollment = enrollmentRepository
                .findByStudent_StudentIdAndSection_SectionId(studentId, enrollmentRequest.getSectionId());
        if (existingEnrollment.isPresent()) {
            throw new ConflictException("Student is already enrolled in this section.");
        }

        // Check section capacity
        long currentEnrollments = enrollmentRepository
                .countBySection_SectionIdAndStatus(enrollmentRequest.getSectionId(), Enrollment.Status.ENROLLED);
        if (currentEnrollments >= section.getMaxSeats()) {
            throw new ConflictException("Section is already full.");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .section(section)
                .status(Enrollment.Status.PENDING)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.mapToEnrollmentResponse(enrollment);

    }

    // get the current student's enrollment list ( EAF)
    @Transactional
    public List<EnrollmentResponse> getMyEnrollments(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_StudentId(studentId);

        if (enrollments.isEmpty()) {
            throw new ResourceNotFoundException("No enrollments found for the given student.");
        }

        return enrollments.stream()
                .map(enrollmentMapper::mapToEnrollmentResponse)
                .toList();
    }



    // update enrollment status - ADMIN ROLE only
    public EnrollmentResponse updateStatus(Long enrollmentId, UpdateEnrollmentRequest updateEnrollmentRequest) {
        // enrollment verification exists
        Enrollment enroll = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        // Role check : Admin only
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        if (!isAdmin) {
            throw new AccessDeniedException("Only administrators can update enrollment statuses.");
        }

        // update enrollment status ( Pending -> Enrolled , enrolled ->dropped )
        enroll.setStatus(updateEnrollmentRequest.getStatus());
        enroll = enrollmentRepository.save(enroll);
        return enrollmentMapper.mapToEnrollmentResponse(enroll);
    }

    // drop enrollment -( student or ADMIN )
    public EnrollmentResponse drop(Long studentId, Long enrollmentId) {
        // verify enrollment exists
        Enrollment enroll = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        // Authentication context for role check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));

        // Students can only drop their own enrollments
        if (!isAdmin && !enroll.getStudent().getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Students can only drop their own enrollments.");
        }

        // Route logic based on Admin role
        if (isAdmin) {
            // hard delete
            enrollmentRepository.delete(enroll);
            return EnrollmentResponse.builder()
                    .message("Enrollment permanently removed (Hard Delete).")
                    .build();
        } else {
            // soft delete
            enroll.setStatus(Enrollment.Status.DROPPED);
            enrollmentRepository.save(enroll);
            return EnrollmentResponse.builder()
                    .message("Soft delete successfully")
                    .build();
        }
    }
}
