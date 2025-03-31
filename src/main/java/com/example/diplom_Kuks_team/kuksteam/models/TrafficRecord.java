package com.example.diplom_Kuks_team.kuksteam.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "traffic_records")
public class TrafficRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String srcIp;
    private String dstIp;
    private int srcPort;
    private int dstPort;
    private String protocol;
    private int bytes;
    private String attackType;
    private LocalDateTime startTime;

    @PrePersist
    public void prePersist() {
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
    }


}
