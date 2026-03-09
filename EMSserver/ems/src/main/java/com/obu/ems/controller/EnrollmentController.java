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

        @PostMapping
        public ResponseEntity<EnrollmentResponse> create(@Valid @RequestBody EnrollmentRequest request) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(enrollmentService.enlistStudent(request.getStudentId(), request));
        }

        @GetMapping("/my/{studentId}")
        public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
                @PathVariable Long studentId) {
            return ResponseEntity.ok(enrollmentService.getMyEnrollments(studentId)) ;
        }


        @PutMapping("/{enrollmentId}/status")
        public ResponseEntity<EnrollmentResponse> updateStatus(@PathVariable Long enrollmentId, @Valid @RequestBody UpdateEnrollmentRequest request) {
            return ResponseEntity.ok(enrollmentService.updateStatus(enrollmentId, request));
        }

        @DeleteMapping("/{enrollmentId}")
        public ResponseEntity<String> drop(
                @PathVariable Long enrollmentId, @RequestParam Long studentId) {

            enrollmentService.drop(studentId, enrollmentId);

            return ResponseEntity.ok("Enrollment deleted");
        }
    }
