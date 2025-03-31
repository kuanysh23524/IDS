package com.example.diplom_Kuks_team.kuksteam.repositories;

import com.example.diplom_Kuks_team.kuksteam.models.NetworkDevices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NetworkDevicesRepository extends JpaRepository<NetworkDevices, Integer> {
    Optional<NetworkDevices> findByName(String name);

    Optional<NetworkDevices> findById(Long id);
}
