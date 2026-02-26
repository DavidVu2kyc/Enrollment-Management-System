package com.obu.ems.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    public enum DayOfWeek {
        MON, TUE, WED, THU, FRI, SAT, SUN
    }

    /**
     * Returns true if this schedule overlaps with another.
     * Two schedules conflict when they share the same day AND their time ranges overlap.
     */
    public boolean conflictsWith(Schedule other) {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false;
        }
        // Overlap: not (end <= other.start OR start >= other.end)
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }
}
