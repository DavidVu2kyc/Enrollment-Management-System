package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.model.*;
import com.obu.ems.repository.UserRepository;
import com.obu.ems.repository.StudentRepository;
import com.obu.ems.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping()
    public ResponseEntity<EnrollmentResponse> enrollNew(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enlistStudent(request.getStudentId(), request));
    }

    @GetMapping("/my/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getMyEnrollments(studentId));
    }


    @PutMapping("/{enrollmentId}/registration")
    public ResponseEntity<EnrollmentResponse> updateEnrollmentRegistration(@PathVariable Long enrollmentId, @PathVariable UpdateEnrollmentRequest request) {
        //    pending to enrolled status
        return ResponseEntity.ok(enrollmentService.updateRegistration(enrollmentId, request));
    }

    //    update changes on enrollment details ( enrollmentId , sectionId  amd status ) PENDING -> ENROLLED
    @PutMapping("/{enrollmentId}/apply")
    public ResponseEntity<EnrollmentResponse> updateEnrollmentSelection(@PathVariable Long enrollmentId, @Valid @RequestBody UpdateEnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.confirmChanges(enrollmentId, request));
    }

    @PutMapping("/{enrollmentId}/confirm")
    public ResponseEntity<EnrollmentResponse> confirm(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.confirmRegistration(enrollmentId));
    }


    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponse> getEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollment(enrollmentId));
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<String> drop(
            @PathVariable Long enrollmentId, @RequestParam Long studentId) {

        enrollmentService.drop(studentId, enrollmentId);

        return ResponseEntity.ok("Enrollment deleted");
    }
}
