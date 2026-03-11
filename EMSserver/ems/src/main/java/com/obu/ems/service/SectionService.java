package com.obu.ems.service;

import com.obu.ems.dto.*;
import com.obu.ems.mapper.EnrollmentMapper;
import com.obu.ems.mapper.SectionMapper;
import com.obu.ems.model.Enrollment;
import com.obu.ems.model.Section;
import com.obu.ems.repository.EnrollmentRepository;
import com.obu.ems.repository.SectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SectionMapper sectionMapper;
    private final EnrollmentMapper enrollmentMapper;

    // list sections
    @Transactional(readOnly = true)
    public List<SectionResponse> getAll() {
        List<Section> sections = sectionRepository.findAllWithDetails();

        return sections.stream()
                .map(sectionMapper::mapToSectionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getByIds(List<Long> ids) {
        List<Section> sections = sectionRepository.findAllById(ids);
        return sections.stream()
                .map(sectionMapper::mapToSectionResponse)
                .toList();
    }

    // get section details
    @Transactional(readOnly = true)
    public SectionResponse getById(Long sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Section not found with id: " + sectionId));

        return sectionMapper.mapToSectionResponse(section);
    }

    @Transactional
    // A list of students enrolled in a section - admin permission only
    public List<EnrollmentResponse> getEnrollmentsBySection(Long sectionId) {
        // admin permission check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities()
                .stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("You are not allowed to view this resource");
        }
        // Entity mapping (section repo )
        List<Enrollment> enrollments = enrollmentRepository.findBySection_SectionId(sectionId);

        if (enrollments.isEmpty()) {
            throw new EntityNotFoundException("No Enrollments found for the given sectionId: " + sectionId);
        }

        return enrollments.stream()
                .map(enrollmentMapper::mapToEnrollmentResponse)
                .toList();
    }
}
