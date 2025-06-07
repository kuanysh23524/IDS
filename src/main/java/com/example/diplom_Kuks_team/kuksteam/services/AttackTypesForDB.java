package com.example.diplom_Kuks_team.kuksteam.services;

import com.example.diplom_Kuks_team.kuksteam.enums.AttackTypes;
import com.example.diplom_Kuks_team.kuksteam.models.AttackTypesInDB;
import com.example.diplom_Kuks_team.kuksteam.repositories.AttackTypesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AttackTypesForDB {
    @Autowired
    AttackTypesRepository attackTypesRepository;
    AttackTypes[] attackTypes = AttackTypes.values();

    @PostConstruct
    public void saveAttackTypesInDB() {

        for (AttackTypes attackTypes1 : attackTypes) {
            Optional<AttackTypesInDB> checkExisting = attackTypesRepository.findByName(attackTypes1.name());

            if (checkExisting.isEmpty()) {
                AttackTypesInDB attackTypesInDB = new AttackTypesInDB();
                attackTypesInDB.setName(attackTypes1.name());
                attackTypesRepository.save(attackTypesInDB);
            }
        }
    }

}
