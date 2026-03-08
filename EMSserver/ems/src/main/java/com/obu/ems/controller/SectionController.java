package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    // list all sections
    @GetMapping
    public ResponseEntity<List<SectionResponse>> getAll(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long termId) {
        return ResponseEntity.ok(sectionService.getAll(courseId, termId));
    }

    // get section details by sectionId
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> getById(@PathVariable Long sectionId) {
        return ResponseEntity.ok(sectionService.getById(sectionId));
    }

    // get enrollment list of students in a section -aADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{sectionId}/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollments(@PathVariable Long sectionId) {
        return ResponseEntity.ok(sectionService.getEnrollmentsBySection(sectionId));
    }
}
