package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.service.TermService;
import com.obu.ems.service.DegreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermDegreeController {

    private final TermService termService;
    private final DegreeService degreeService;

    @GetMapping
    public ResponseEntity<List<TermResponse>> getAllTerms() {
        return ResponseEntity.ok(termService.getAll());
    }

    // returrn active terms
    @GetMapping("/active")
    public ResponseEntity<TermResponse> getActiveTerm(Boolean active) {
        return ResponseEntity.ok(termService.getActiveTerm(active));
    }

    // List all degrees
    @GetMapping("/degrees")
    public ResponseEntity<List<DegreeResponse>> getAllDegrees() {
        return ResponseEntity.ok(degreeService.getAll());
    }

    // Get a list of courses associated withj degree
    @GetMapping("/degrees/{degreeId}/courses")
    public ResponseEntity<List<CourseResponse>> getCoursesByDegree(@PathVariable Long degreeId) {
        return ResponseEntity.ok(degreeService.getCoursesByDegree(degreeId));
    }
}
