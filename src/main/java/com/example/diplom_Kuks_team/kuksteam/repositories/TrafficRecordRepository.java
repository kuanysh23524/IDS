package com.example.diplom_Kuks_team.kuksteam.repositories;

import com.example.diplom_Kuks_team.kuksteam.models.TrafficRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficRecordRepository extends JpaRepository<TrafficRecord, Long> {
}
