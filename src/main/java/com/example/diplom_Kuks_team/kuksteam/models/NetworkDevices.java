package com.example.diplom_Kuks_team.kuksteam.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "network_devices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDevices {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String name;
    String description;
}
