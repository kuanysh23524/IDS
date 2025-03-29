package com.example.diplom_Kuks_team.kuksteam.repositories;

import com.example.diplom_Kuks_team.kuksteam.models.AttackLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.LinkOption;

@Repository
public interface AttackLogRepository extends JpaRepository<AttackLog, Long> {
}
