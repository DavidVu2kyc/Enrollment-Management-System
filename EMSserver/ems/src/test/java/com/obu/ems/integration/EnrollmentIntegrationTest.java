package com.obu.ems.integration;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.obu.ems.model.Course;
import com.obu.ems.model.Section;
import com.obu.ems.model.Student;
import com.obu.ems.model.Term;
import com.obu.ems.repository.CourseRepository;
import com.obu.ems.repository.SectionRepository;
import com.obu.ems.repository.StudentRepository;
import com.obu.ems.repository.TermRepository;

@AutoConfigureMockMvc
public class EnrollmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private TermRepository termRepository;
    
    @Autowired
    private CourseRepository courseRepository;

    private Long studentId;
    private Long sectionId;

    @BeforeEach
    void setUp() {
       Term term = Term.builder().name("Fall 2026").build();
       term = termRepository.save(term);


       Course course = Course.builder().code("CS101").title("Intro to CS").build();
       course = courseRepository.save(course);

       Section section = Section.builder()
               .sectionCode("CS101-A")
               .term(term)
               .course(course)
               .maxSeats(30)
               .build();
       section = sectionRepository.save(section);
       sectionId = section.getSectionId();


       Student student = Student.builder()
               .firstName("Alice")
               .lastName("Smith")
               .studentNumber("S101")
               .build();
       student = studentRepository.save(student);
       studentId = student.getStudentId();


    }

    // successful enlistment ( E2E)

    @Test
    void testSuccessfulEnlistment() {
        
    }
    // prevent duplicate enlistment 
    
    // section capacity hard limit 
    
    // admin approves enrollment 
    
    // security - studetn can not update status
    
    // student -soft delete own course
    
    // admin hard delete enrollment



}
