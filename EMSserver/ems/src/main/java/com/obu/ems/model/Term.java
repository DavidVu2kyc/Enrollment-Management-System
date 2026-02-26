package com.obu.ems.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"year", "semester"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long termId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Semester semester;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    public enum Semester {
        FIRST, SECOND, SUMMER
    }
}
