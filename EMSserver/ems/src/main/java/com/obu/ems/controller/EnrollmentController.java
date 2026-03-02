package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.model.*;
import com.obu.ems.repository.UserRepository;
import com.obu.ems.repository.StudentRepository;
import com.obu.ems.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class `EnrollmentController {

    // private final EnrollmentService enrollmentService;
    // private final UserRepository userRepository;
    // private final StudentRepository studentRepository;

    // @PostMapping
    // public ResponseEntity<EnrollmentResponse> create(@Valid @RequestBody EnrollmentRequest request,
    //                                                  @AuthenticationPrincipal UserDetails userDetails) {
    //     User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    //     Long studentId = request.getStudentId();

    //     // If no specific studentId in request, use the authenticated user's own student profile
    //     if (studentId == null) {
    //         Student student = studentRepository.findByUser_UserId(user.getUserId()).orElseThrow();
    //         studentId = student.getStudentId();
    //     }

    //     return ResponseEntity.status(HttpStatus.CREATED)
    //             .body(enrollmentService.enlist(studentId, request.getSectionId()));
    // }

    // @GetMapping("/my")
    // public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
    //         @AuthenticationPrincipal UserDetails userDetails,
    //         @RequestParam(required = false) Long termId,
    //         @RequestParam(required = false) Enrollment.Status status) {
    //     User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    //     Student student = studentRepository.findByUser_UserId(user.getUserId()).orElseThrow();
    //     return ResponseEntity.ok(enrollmentService.getMyEnrollments(student.getStudentId(), termId, status));
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<EnrollmentResponse> getById(@PathVariable Long id) {
    //     return ResponseEntity.ok(enrollmentService.getById(id));
    // }

    // @PutMapping("/{id}")
    // public ResponseEntity<EnrollmentResponse> updateStatus(@PathVariable Long id,
    //                                                        @Valid @RequestBody UpdateEnrollmentRequest request) {
    //     return ResponseEntity.ok(enrollmentService.updateStatus(id, request.getStatus()));
    // }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> drop(@PathVariable Long id,
    //                                  @AuthenticationPrincipal UserDetails userDetails) {
    //     User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    //     boolean isAdmin = user.getRole() == User.Role.ADMIN;
    //     Long studentId = null;
    //     if (!isAdmin) {
    //         studentId = studentRepository.findByUser_UserId(user.getUserId())
    //                 .orElseThrow().getStudentId();
    //     }
    //     enrollmentService.drop(id, studentId, isAdmin);
    //     return ResponseEntity.noContent().build();
    // }


}
