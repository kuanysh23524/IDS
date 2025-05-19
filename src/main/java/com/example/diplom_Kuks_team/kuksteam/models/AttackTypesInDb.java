package com.example.diplom_Kuks_team.kuksteam.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attack_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AttackTypesInDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    public AttackTypesInDb(String name) {
        this.name = name;
    }
}
