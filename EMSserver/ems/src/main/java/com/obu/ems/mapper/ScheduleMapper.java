package com.obu.ems.mapper;

import com.obu.ems.dto.ScheduleResponse;
import com.obu.ems.model.Schedule;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {
    public ScheduleResponse mapToScheduleResponse(Schedule schedule) {
        if (schedule == null) {
            return null;
        }
        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .dayOfWeek(schedule.getDayOfWeek() != null ? schedule.getDayOfWeek().name() : null)
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .build();
    }
}
