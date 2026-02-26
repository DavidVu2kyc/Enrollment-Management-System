package com.obu.ems.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalTime;

@Data
@Builder
public class ScheduleResponse {
    private Long scheduleId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
