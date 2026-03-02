package com.obu.ems.mapper;

import com.obu.ems.dto.SectionResponse;
import com.obu.ems.model.Section;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SectionMapper {

    private final CourseMapper courseMapper;
    private final TermMapper termMapper;
    private final ScheduleMapper scheduleMapper;
    private final RoomMapper roomMapper;

    public SectionResponse mapToSectionResponse(Section section) {
        if (section == null) {
            return null;
        }
        return SectionResponse.builder()
                .sectionId(section.getSectionId())
                .sectionCode(section.getSectionCode())
                .course(courseMapper.mapToCourseResponse(section.getCourse()))
                .term(termMapper.mapToTermResponse(section.getTerm()))
                .schedule(scheduleMapper.mapToScheduleResponse(section.getSchedule()))
                .room(roomMapper.mapToRoomResponse(section.getRoom()))
                .facultyName(section.getFacultyName())
                .maxSeats(section.getMaxSeats())
                .enrolledCount(section.getEnrolledCount())
                .availableSeats(section.getAvailableSeats())
                .build();
    }
}
