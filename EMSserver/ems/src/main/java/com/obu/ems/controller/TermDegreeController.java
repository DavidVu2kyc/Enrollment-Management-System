package com.obu.ems.controller;

import com.obu.ems.dto.*;
import com.obu.ems.service.TermService;
import com.obu.ems.service.DegreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TermDegreeController {

    // private final TermService termService;
    private final DegreeService degreeService;

    // @GetMapping("/api/terms")
    // public ResponseEntity<List<TermResponse>> getAllTerms() {
    //     return ResponseEntity.ok(termService.getAll());
    // }

    // @GetMapping("/api/terms/active")
    // public ResponseEntity<TermResponse> getActiveTerm() {
    //     return ResponseEntity.ok(termService.getActiveTerm());
    // }

    //    List all degrees
    @GetMapping("/api/degrees")
    public ResponseEntity<List<DegreeResponse>> getAllDegrees() {
        return ResponseEntity.ok(degreeService.getAll());
    }

    //    Get courses by id
     @GetMapping("/api/degrees/{id}/courses")
     public ResponseEntity<List<CourseResponse>> getCoursesByDegree(@PathVariable Long id) {
         return ResponseEntity.ok(degreeService.getCoursesByDegree(id));
     }
}
