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
public class eEnrollmentController {

     private final EnrollmentService enrollmentService;


     @PostMapping
     public ResponseEntity<EnrollmentResponse> create(@Valid @RequestBody EnrollmentRequest request) {

         return ResponseEntity.status(HttpStatus.CREATED)
                 .body(enrollmentService.enlistStudent(request.getStudentId(), request));
     }

     @GetMapping("/my")
     public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(
             @AuthenticationPrincipal UserDetails userDetails,
             @RequestParam(required = false) Long termId,
             @Valid @RequestBody  EnrollmentRequest  enrollmentRequest) {

         return ResponseEntity.ok(enrollmentService.getMyEnrollments(termId, enrollmentRequest));
     }

     @GetMapping("/{id}")
     public ResponseEntity<EnrollmentResponse> getById(@PathVariable Long id) {
         return ResponseEntity.ok(enrollmentService.getById(id));
     }

     @PutMapping("/{id}")
     public ResponseEntity<EnrollmentResponse> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody EnrollmentRequest enrollmentRequest,
                                                            @Valid @RequestBody UpdateEnrollmentRequest request) {

         return ResponseEntity.ok(enrollmentService.updateStatus(id,enrollmentRequest, request));
     }

     @DeleteMapping("/{studentId}")
     public ResponseEntity<Void> drop(@PathVariable Long studentId , @Valid @RequestBody EnrollmentRequest request) {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isAdmin =  authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

         enrollmentService.drop(studentId,isAdmin,request);

         return ResponseEntity.noContent().build();
     }


}
