package com.example.diplom_Kuks_team.kuksteam.services;


import com.example.diplom_Kuks_team.kuksteam.models.AttackLog;
import com.example.diplom_Kuks_team.kuksteam.repositories.AttackLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttackLogService {
    private final AttackLogRepository attackLogRepository;

    public List<AttackLog> getAllLogs() {
        return attackLogRepository.findAll();
    }

    public void saveLog(AttackLog log) {
        attackLogRepository.save(log);
    }
}
