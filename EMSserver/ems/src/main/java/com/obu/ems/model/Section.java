package com.obu.ems.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"term_id", "section_code"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "section_code", nullable = false, length = 20)
    private String sectionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Column(name = "faculty_name", length = 200)
    private String facultyName;

    @Column(name = "max_seats", nullable = false)
    private Integer maxSeats;

    @Column(name = "enrolled_count", nullable = false)
    private Integer enrolledCount = 0;

    public boolean isFull() {
        return enrolledCount >= maxSeats;
    }

    public int getAvailableSeats() {
        return maxSeats - enrolledCount;
    }
}
