package com.obu.ems.integration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.obu.ems.dto.EnrollmentRequest;
import com.obu.ems.dto.UpdateEnrollmentRequest;
import com.obu.ems.model.*;
import com.obu.ems.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class EnrollmentIntegrationTest extends BaseIntegrationTest {

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

        @Autowired
        private DegreeRepository degreeRepository;

        @Autowired
        private EnrollmentRepository enrollmentRepository;

        private Long studentId;
        private Long sectionId;
        private String studentUsername = "teststudent";
        private String adminUsername = "testadmin";

        @BeforeEach
        void setUp() {
                enrollmentRepository.deleteAll();
                sectionRepository.deleteAll();
                courseRepository.deleteAll();
                termRepository.deleteAll();
                studentRepository.deleteAll();
                userRepository.deleteAll();
                degreeRepository.deleteAll();

                Degree degree = degreeRepository.save(Degree.builder().name("BSCS").build());

                User studentUser = createTestUser(studentUsername, "password", User.Role.STUDENT);
                createTestUser(adminUsername, "password", User.Role.ADMIN);

                Student student = Student.builder()
                                .firstName("Alice")
                                .lastName("Smith")
                                .studentNumber("S101")
                                .degree(degree)
                                .user(studentUser)
                                .build();

                student = studentRepository.save(student);
                studentId = student.getStudentId();

                Term term = termRepository.save(Term.builder().name("Fall 2026").build());
                Course course = courseRepository.save(Course.builder().code("CS101").title("Intro to CS").build());

                Section section = Section.builder()
                                .sectionCode("CS101-A")
                                .term(term)
                                .course(course)
                                .maxSeats(30)
                                .build();
                section = sectionRepository.save(section);
                sectionId = section.getSectionId();
        }

        // cover e2e enlistment case
        @Test
        void testSuccessfulEnlistment() throws Exception {
                EnrollmentRequest request = new EnrollmentRequest();
                request.setSectionId(sectionId);
                request.setStudentId(studentId);

                getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.status", is("PENDING")))
                                .andExpect(jsonPath("$.studentId", is(studentId.intValue())))
                                .andExpect(jsonPath("$.sectionId", is(sectionId.intValue())));
        }

        // cover e2e prevent duplicate enlistment case
        @Test
        void testPreventDuplicateEnlistment() throws Exception {
                EnrollmentRequest request = new EnrollmentRequest();
                request.setSectionId(sectionId);
                request.setStudentId(studentId);

                // First enlistment
                getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());

                // Second enlistment (duplicate)
                getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isConflict());
        }

        // cover e2e section capacity hard limit case
        @Test
        void testSectionCapacityHardLimit() throws Exception {
                // Set capacity to 1
                Section section = sectionRepository.findById(sectionId).get();
                section.setMaxSeats(1);
                sectionRepository.save(section);

                EnrollmentRequest request = new EnrollmentRequest();
                request.setSectionId(sectionId);
                request.setStudentId(studentId);

                // First student fills the section
                getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());

                // Create another student
                Degree degree = degreeRepository.findAll().get(0);
                User user2 = createTestUser("student2", "password", User.Role.STUDENT);
                Student student2 = studentRepository.save(Student.builder()
                                .firstName("Bob")
                                .lastName("Jones")
                                .studentNumber("S102")
                                .degree(degree)
                                .user(user2)
                                .build());

                EnrollmentRequest request2 = new EnrollmentRequest();
                request2.setSectionId(sectionId);
                request2.setStudentId(student2.getStudentId());

                // Second student tries to enlist in full section
                getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders("student2"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isBadRequest());
        }

        // cover e2e admin approve enrollment case
        @Test
        void testAdminApprovesEnrollment() throws Exception {
                // Student enlists
                EnrollmentRequest request = new EnrollmentRequest();
                request.setSectionId(sectionId);
                request.setStudentId(studentId);

                String response = getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andReturn().getResponse().getContentAsString();

                Long enrollmentId = objectMapper.readTree(response).get("enrollmentId").asLong();

                // Admin approves
                UpdateEnrollmentRequest updateRequest = new UpdateEnrollmentRequest();
                updateRequest.setStatus(Enrollment.Status.ENROLLED);

                getMockMvc().perform(put("/api/enrollments/" + enrollmentId + "/status")
                                .headers(authenticatedHeaders(adminUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is("ENROLLED")));
        }

        // cover e2e student cannot update status case
        @Test
        void testSecurityStudentCannotUpdateStatus() throws Exception {
                // Student enlists
                EnrollmentRequest request = new EnrollmentRequest();
                request.setSectionId(sectionId);
                request.setStudentId(studentId);

                String response = getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andReturn().getResponse().getContentAsString();

                Long enrollmentId = objectMapper.readTree(response).get("enrollmentId").asLong();

                // Student tries to approve own enrollment
                UpdateEnrollmentRequest updateRequest = new UpdateEnrollmentRequest();
                updateRequest.setStatus(Enrollment.Status.ENROLLED);

                getMockMvc().perform(put("/api/enrollments/" + enrollmentId + "/status")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isForbidden());
        }

        @Test
        void testStudentDropCourse() throws Exception {
                // Enlist first
                EnrollmentRequest request = new EnrollmentRequest();
                request.setSectionId(sectionId);
                request.setStudentId(studentId);

                String responseBody = getMockMvc().perform(post("/api/enrollments")
                                .headers(authenticatedHeaders(studentUsername))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andReturn().getResponse().getContentAsString();

                Long enrollmentId = objectMapper.readTree(responseBody).get("enrollmentId").asLong();

                // Drop
                getMockMvc().perform(delete("/api/enrollments/" + enrollmentId)
                                .headers(authenticatedHeaders(studentUsername))
                                .param("studentId", studentId.toString()))
                                .andExpect(status().isOk());

                // Verify it's dropped (soft delete)
                Enrollment updatedEnrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
                assertEquals(Enrollment.Status.DROPPED, updatedEnrollment.getStatus());
        }
}
