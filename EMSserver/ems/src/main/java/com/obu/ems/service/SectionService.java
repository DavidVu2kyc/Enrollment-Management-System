package com.obu.ems.service;

import com.obu.ems.dto.EnrollmentResponse;
import com.obu.ems.dto.SectionResponse;
import com.obu.ems.model.Section;
import com.obu.ems.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionService {

    private SectionRepository sectionRepository;

    //    map to SectionResponse
    public SectionResponse mapToSectionResponse(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getSectionId())
                .sectionCode(section.getSectionCode())
                .course(section.getCourseResponse())
                .term(section.getTermResponse())
                .schedule(section.getScheduleResponse())
                .room(section.getSectionResponse().getRoom())
                .build();

    }

    public SectionResponse mapToSectionResponseDetails(Section section )
    {
        return SectionResponse.builder()
                .sectionId(section.getSectionId())
                .sectionCode(section.getSectionCode())
                .course(section.getCourseResponse())
                .term(section.getTermResponse())
                .schedule(section.getScheduleResponse())
                .room(section.getSectionResponse().getRoom())
                .facultyName(section.getFacultyName())
                .maxSeats(section.getSectionResponse().getMaxSeats())
                .enrolledCount(section.getSectionResponse().getEnrolledCount())
                .build();

    }

    //    list sections
    public List<SectionResponse> getAll(Long courseId, Long termId){
        List<Section> section  = sectionRepository.findByCourse_CourseIdAndTerm_TermId( courseId, termId);
        return section.stream().map(this::mapToSectionResponse).toList();

    }

    //   get section details by id
    public SectionResponse getById(Long sectionId)
    {
        Section sectionDetails = sectionRepository.findBy_sectionId(sectionId);
        return mapToSectionResponse(sectionDetails);
    }

    //    list students enrolled in a section
    public  List<EnrollmentResponse> getEnrollmentsBySection(Long sectionId){

    }


}
