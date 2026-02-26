package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.service.SectionService;
import com.obu.ems.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    // private final SectionService sectionService;
    // private final EnrollmentService enrollmentService;

    // @GetMapping
    // public ResponseEntity<List<SectionResponse>> getAll(
    //         @RequestParam(required = false) Long termId,
    //         @RequestParam(required = false) Long courseId) {
    //     return ResponseEntity.ok(sectionService.getAll(termId, courseId));
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<SectionResponse> getById(@PathVariable Long id) {
    //     return ResponseEntity.ok(sectionService.getById(id));
    // }

    // @GetMapping("/{id}/enrollments")
    // public ResponseEntity<List<EnrollmentResponse>> getEnrollments(@PathVariable Long id) {
    //     return ResponseEntity.ok(enrollmentService.getEnrollmentsBySection(id));
    // }
}
