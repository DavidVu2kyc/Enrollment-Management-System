package com.obu.ems.service;

import com.obu.ems.exception.ResourceNotFoundException;
import com.obu.ems.mapper.EnrollmentMapper;
import com.obu.ems.model.Enrollment;
import com.obu.ems.model.Student;
import com.obu.ems.model.User;
import com.obu.ems.repository.EnrollmentRepository;
import com.obu.ems.repository.SectionRepository;
import com.obu.ems.repository.StudentRepository;
import com.obu.ems.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private EnrollmentMapper enrollmentMapper;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    // verify soft delte to ENROLE prop
    @Test
    void drop_ShouldSetStatusToDropped_WhenStudentIsOwner() {
        // Arrange
        Long studentId = 1L;
        Long enrollmentId = 10L;

        Student student = Student.builder().studentId(studentId).build();
        Enrollment enrollment = Enrollment.builder()
                .enrollmentId(enrollmentId)
                .student(student)
                .status(Enrollment.Status.PENDING)
                .build();

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        // Mock security context for student role
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("testuser", "pass",
                Collections.emptyList()); // No ROLE_ADMIN
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(auth);

        // Act
        enrollmentService.drop(studentId, enrollmentId);

        // Assert
        assertEquals(Enrollment.Status.DROPPED, enrollment.getStatus());
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void drop_ShouldThrowException_WhenEnrollmentNotFound() {
        // Arrange
        Long studentId = 1L;
        Long enrollmentId = 10L;
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.drop(studentId, enrollmentId));
    }
}
