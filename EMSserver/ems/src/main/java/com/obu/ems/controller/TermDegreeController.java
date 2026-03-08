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

     private final TermService termService;
    private final DegreeService degreeService;

     @GetMapping("/api/terms")
     public ResponseEntity<List<TermResponse>> getAllTerms() {
         return ResponseEntity.ok(termService.getAll());
     }

//     returrn active terms
     @GetMapping("/api/terms/active")
     public ResponseEntity<TermResponse> getActiveTerm(Boolean active) {
         return ResponseEntity.ok(termService.getActiveTerm(active));
     }

    //    List all degrees
    @GetMapping("/api/degrees")
    public ResponseEntity<List<DegreeResponse>> getAllDegrees() {
        return ResponseEntity.ok(degreeService.getAll());
    }

    //    Get a list of courses associated withj degree
     @GetMapping("/api/degrees/{degreeId}/courses")
     public ResponseEntity<List<CourseResponse>> getCoursesByDegree(@PathVariable Long degreeId) {
         return ResponseEntity.ok(degreeService.getCoursesByDegree(degreeId));
     }
}
