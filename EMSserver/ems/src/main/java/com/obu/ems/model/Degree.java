package com.obu.ems.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "degrees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Degree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "degree_id")
        private Long degreeId;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}
