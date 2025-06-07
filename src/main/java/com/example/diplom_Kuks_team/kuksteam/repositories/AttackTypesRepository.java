package com.example.diplom_Kuks_team.kuksteam.repositories;

import com.example.diplom_Kuks_team.kuksteam.models.AttackTypesInDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttackTypesRepository extends JpaRepository<AttackTypesInDB,Long> {
Optional<AttackTypesInDB> findByName(String name);
}
