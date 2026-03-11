package com.obu.ems.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(SectionController.class);
    private final SectionService sectionService;

    // list all sections- browse a list of section available
    @GetMapping
    public ResponseEntity<List<SectionResponse>> getAllSections() {
        logger.info("Request received: Get all sections");

        List<SectionResponse> sections = sectionService.getAll();

        logger.info("Returning {} sections", sections.size());

        return ResponseEntity.ok(sections);
    }

    @GetMapping("/bulk")
    public ResponseEntity<List<SectionResponse>> getSectionsBulk(@RequestParam List<Long> ids) {
        logger.info("Request received: Get sections bulk for ids {}", ids);
        return ResponseEntity.ok(sectionService.getByIds(ids));
    }

    // get section details by sectionId -view section details
    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> getById(@PathVariable Long sectionId) {

        logger.info("Request received: Get section by id {}", sectionId);

        SectionResponse section = sectionService.getById(sectionId);

        logger.info("Section {} retrieved successfully", sectionId);

        return ResponseEntity.ok(section);
    }

    // get enrollment list of students in a section - ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{sectionId}/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollments(@PathVariable Long sectionId) {

        logger.info("Admin request: Get enrollments for section {}", sectionId);

        List<EnrollmentResponse> enrollments = sectionService.getEnrollmentsBySection(sectionId);

        logger.info("Returning {} enrollments for section {}", enrollments.size(), sectionId);

        return ResponseEntity.ok(enrollments);
    }

}
