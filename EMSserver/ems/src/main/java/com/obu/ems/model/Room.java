package com.obu.ems.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"building", "room_number"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(nullable = false, length = 100)
    private String building;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(nullable = false)
    private Integer capacity;
}
