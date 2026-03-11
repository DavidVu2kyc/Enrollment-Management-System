package com.obu.ems.service;

import com.obu.ems.dto.EnrollmentRequest;
import com.obu.ems.dto.EnrollmentResponse;
import com.obu.ems.dto.UpdateEnrollmentRequest;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    // enlist a student in a section or multiple sections
    @Transactional
    public EnrollmentResponse enlistStudent(Long studentId, EnrollmentRequest enrollmentRequest) {
        Student student;
        
        // Resolve student: 1. From path, 2. From body, 3. From security context
        if (studentId != null) {
            student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found."));
        } else if (enrollmentRequest.getStudentId() != null) {
            student = studentRepository.findById(enrollmentRequest.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found."));
        } else {
            student = getCurrentStudent();
        }

        // Bulk enrollment case
        if (enrollmentRequest.getSectionIds() != null && !enrollmentRequest.getSectionIds().isEmpty()) {
            return enlistStudentBulk(student, enrollmentRequest);
        }

        // Single enrollment case
        if (enrollmentRequest.getSectionId() == null) {
            throw new BadRequestException("Section ID must be provided.");
        }

        Section section = sectionRepository.findById(enrollmentRequest.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found."));

        // Check for conflicts and capacity
        String validationError = validateEnrollment(student, section);
        if (validationError != null) {
            throw new ConflictException(validationError);
        }

        // Check if already enrolled in this specific section
        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudent_StudentIdAndSection_SectionId(student.getStudentId(), enrollmentRequest.getSectionId());
        if (existingEnrollment.isPresent()) {
            throw new ConflictException("Student is already enrolled in this exact section.");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .section(section)
                .status(Enrollment.Status.PENDING)
                .build();

        enrollment = enrollmentRepository.save(enrollment);

        return enrollmentMapper.mapToEnrollmentResponse(enrollment);
    }

    private Student getCurrentStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User must be authenticated to perform this action.");
        }
        
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
                
        return studentRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + username));
    }

    @Transactional
    private EnrollmentResponse enlistStudentBulk(Student student, EnrollmentRequest request) {
        // Filter out any null IDs to prevent "The given id must not be null" 500 error
        List<Long> cleanIds = request.getSectionIds().stream()
                .filter(java.util.Objects::nonNull)
                .toList();

        if (cleanIds.isEmpty()) {
            throw new BadRequestException("No valid section IDs provided.");
        }

        List<Section> sections = sectionRepository.findAllById(cleanIds);
        
        if (sections.size() != cleanIds.size()) {
            throw new ResourceNotFoundException("One or more sections not found.");
        }

        // 1. Check for internal overlaps between sections
        for (int i = 0; i < sections.size(); i++) {
            for (int j = i + 1; j < sections.size(); j++) {
                Section s1 = sections.get(i);
                Section s2 = sections.get(j);
                if (s1.getSchedule() != null && s2.getSchedule() != null && s1.getSchedule().conflictsWith(s2.getSchedule())) {
                    throw new ConflictException(String.format("Internal schedule conflict: '%s' overlaps with '%s'", 
                        s1.getSectionCode(), s2.getSectionCode()));
                }
            }
        }

        // 2. Validate each section against student's existing schedule and capacity
        for (Section section : sections) {
            String validationError = validateEnrollment(student, section);
            if (validationError != null) {
                throw new ConflictException(validationError);
            }
            
            // Check if already enrolled
            Optional<Enrollment> existing = enrollmentRepository.findByStudent_StudentIdAndSection_SectionId(student.getStudentId(), section.getSectionId());
            if (existing.isPresent()) {
                throw new ConflictException("Student is already enrolled in section: " + section.getSectionCode());
            }
        }

        // 3. Save all enrollments
        Enrollment lastEnrollment = null;
        for (Section section : sections) {
            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .section(section)
                    .status(Enrollment.Status.PENDING)
                    .build();
            lastEnrollment = enrollmentRepository.save(enrollment);
        }

        EnrollmentResponse response = enrollmentMapper.mapToEnrollmentResponse(lastEnrollment);
        response.setMessage("Successfully enrolled in " + sections.size() + " sections.");
        return response;
    }

    @Transactional
    public EnrollmentResponse getEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));
        return enrollmentMapper.mapToEnrollmentResponse(enrollment);
    }

    // get the current student's enrollment list ( EAF)
    @Transactional
    public List<EnrollmentResponse> getMyEnrollments(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudent_StudentId(studentId);

        if (enrollments.isEmpty()) {
            throw new ResourceNotFoundException("No enrollments found for the given student.");
        }

        return enrollments.stream().map(enrollmentMapper::mapToEnrollmentResponse).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @enrollmentSecurity.isOwner(#enrollmentId, authentication.name)")
    public EnrollmentResponse updateStatus(Long enrollmentId, UpdateEnrollmentRequest request) {

        Enrollment enroll = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        if (request.getSectionId() != null) {
            Section section = sectionRepository.findById(request.getSectionId()).orElseThrow(() -> new ResourceNotFoundException("Section not found"));
            
            // Check for conflicts and capacity
            String validationError = validateEnrollment(enroll.getStudent(), section);
            if (validationError != null) {
                throw new ConflictException(validationError);
            }

            enroll.setSection(section);
        }

        if (request.getStatus() != null) {
            enroll.setStatus(request.getStatus());
        }

        enroll = enrollmentRepository.save(enroll);
        return enrollmentMapper.mapToEnrollmentResponse(enroll);
    }

    //  confirm registration-apply changes
    @Transactional
    public EnrollmentResponse updateRegistration(Long enrollmentId, UpdateEnrollmentRequest request) {

        Enrollment enroll = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        if (request.getSectionId() != null) {

            Long sectionId = request.getSectionId();

            Section section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

            // Check for conflicts and capacity
            String validationError = validateEnrollment(enroll.getStudent(), section);
            if (validationError != null) {
                throw new ConflictException(validationError);
            }

            enroll.setSection(section);
        }

        if (request.getStatus() != null) {
            enroll.setStatus(request.getStatus());
        }

        enroll = enrollmentRepository.save(enroll);

        return enrollmentMapper.mapToEnrollmentResponse(enroll);
    }

    @Transactional
    public EnrollmentResponse confirmChanges(Long enrollmentId, UpdateEnrollmentRequest request) {
        Enrollment enroll = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        if (request.getSectionId() != null) {
            Section section = sectionRepository.findById(request.getSectionId()).orElseThrow(() -> new ResourceNotFoundException("Section not found"));
            enroll.setSection(section);
        }

        if (request.getStatus() != null) {
            enroll.setStatus(request.getStatus());
        }

        enroll = enrollmentRepository.save(enroll);
        return enrollmentMapper.mapToEnrollmentResponse(enroll);
    }


    @Transactional
    public EnrollmentResponse confirmRegistration(Long enrollmentId) {
        //   verify enrollment existing
        Enrollment enroll = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));


        if (enroll.getStatus() != Enrollment.Status.PENDING) {
            throw new IllegalStateException("Only pending enrollments can be confirmed.");
        }

        enroll.setStatus(Enrollment.Status.ENROLLED);

        enroll = enrollmentRepository.save(enroll);

        return enrollmentMapper.mapToEnrollmentResponse(enroll);
    }

    @Transactional
    public List<EnrollmentResponse> confirmRegistrationBulk(List<Long> enrollmentIds) {
        if (enrollmentIds == null || enrollmentIds.isEmpty()) {
            throw new BadRequestException("Enrollment IDs must be provided.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllById(enrollmentIds);
        
        if (enrollments.size() != enrollmentIds.size()) {
            throw new ResourceNotFoundException("One or more enrollments not found.");
        }

        for (Enrollment enroll : enrollments) {
            if (enroll.getStatus() != Enrollment.Status.PENDING) {
                throw new IllegalStateException("Only pending enrollments can be confirmed: " + enroll.getEnrollmentId());
            }
            enroll.setStatus(Enrollment.Status.ENROLLED);
        }

        return enrollmentRepository.saveAll(enrollments).stream()
                .map(enrollmentMapper::mapToEnrollmentResponse)
                .toList();
    }


    @Transactional
    // drop enrollment -( student or ADMIN )
    public EnrollmentResponse drop(Long studentId, Long enrollmentId) {
        // verify enrollment exists
        Enrollment enroll = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        // Authentication context for role check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Students can only drop their own enrollments
        if (!isAdmin && !enroll.getStudent().getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Students can only drop their own enrollments.");
        }

        // Set status to DROPPED
        enroll.setStatus(Enrollment.Status.DROPPED);
        enrollmentRepository.save(enroll);
        return EnrollmentResponse.builder().message("Soft delete successfully").build();

    }

    /**
     * Validates if a student can enroll in a section.
     * Returns an error message if there's a conflict or capacity issue, otherwise null.
     */
    private String validateEnrollment(Student student, Section section) {
        // 1. Capacity Check
        if (section.isFull()) {
            return "Section is already full. No seats available.";
        }

        // 2. Schedule Conflict Check
        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudent_StudentId(student.getStudentId());
        
        for (Enrollment existing : studentEnrollments) {
            // Only consider active or pending enrollments for conflict checks
            if (existing.getStatus() == Enrollment.Status.ENROLLED || existing.getStatus() == Enrollment.Status.PENDING) {
                
                // If the student is already in the same course, that might be a problem (depending on school policy)
                // But specifically checking schedule overlaps:
                if (existing.getSection().getSchedule() != null && section.getSchedule() != null) {
                    if (existing.getSection().getSchedule().conflictsWith(section.getSchedule())) {
                        return String.format("Schedule conflict: '%s' overlaps with your existing enrollment in '%s' (%s)",
                                section.getSectionCode(),
                                existing.getSection().getCourse().getCode(),
                                existing.getSection().getSectionCode());
                    }
                }
            }
        }
        
        return null;
    }

}
