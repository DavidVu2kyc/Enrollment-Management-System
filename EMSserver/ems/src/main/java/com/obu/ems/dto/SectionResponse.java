package com.obu.ems.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectionResponse {
    private Long sectionId;
    private String sectionCode;
    private CourseResponse course;
    private TermResponse term;
    private ScheduleResponse schedule;
    private RoomResponse room;
    private String facultyName;
    private Integer maxSeats;
    private Integer enrolledCount;
    private Integer availableSeats;

    @Data
    @Builder
    public static class RoomResponse {
        private Long roomId;
        private String building;
        private String roomNumber;
    }
}
