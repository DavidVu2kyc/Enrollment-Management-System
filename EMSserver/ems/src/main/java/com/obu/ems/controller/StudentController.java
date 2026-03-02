package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.model.User;
import com.obu.ems.repository.UserRepository;
import com.obu.ems.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

     private final StudentService studentService;
     private final UserRepository userRepository;

     @GetMapping
     public ResponseEntity<Page<StudentResponse>> getAll(
             @RequestParam(required = false) Long degreeId,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "20") int size) {
         return ResponseEntity.ok(studentService.getAll(degreeId, PageRequest.of(page, size)));
     }

     @GetMapping("/me")
     public ResponseEntity<StudentResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
         User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
         return ResponseEntity.ok(studentService.getByUserId(user.getUserId()));
     }

     @PutMapping("/{id}")
     public ResponseEntity<StudentResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateStudentRequest request) {
         return ResponseEntity.ok(studentService.updateStudentRequest(id, request));
     }
}
