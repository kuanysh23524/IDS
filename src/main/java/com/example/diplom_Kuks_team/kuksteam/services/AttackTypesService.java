package com.example.diplom_Kuks_team.kuksteam.services;

import com.example.diplom_Kuks_team.kuksteam.enums.AttackTypes;
import com.example.diplom_Kuks_team.kuksteam.models.AttackTypesInDb;
import com.example.diplom_Kuks_team.kuksteam.repositories.AttackTypesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AttackTypesService {
    @Autowired
    AttackTypesRepository attackTypesRepository;

    @PostConstruct
    public void saveAllTypes() {
        List<AttackTypes> attackTypesEnumsArray = Arrays.asList(AttackTypes.values());
        List<AttackTypesInDb> attackTypesInDbsForAdd = new ArrayList<>();

        for (AttackTypes attackTypes1 : attackTypesEnumsArray) {
            AttackTypesInDb attackTypesInDb = new AttackTypesInDb(attackTypes1.name());
            attackTypesInDbsForAdd.add(attackTypesInDb);
        }
        attackTypesRepository.saveAll(attackTypesInDbsForAdd);
    }
}
