package com.example.diplom_Kuks_team.kuksteam.controllers;


import com.example.diplom_Kuks_team.kuksteam.models.AttackLog;
import com.example.diplom_Kuks_team.kuksteam.services.AttackLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/attacks")
public class AttackController {
    private final AttackLogService attackLogService;

    @GetMapping
    public String listAttacks(Model model) {
        List<AttackLog> logs = attackLogService.getAllLogs();
        model.addAttribute("logs", logs);
        return "attack_logs";
    }

    @PostMapping("/add")
    public String addAttack(@RequestParam String ip, @RequestParam String attackType) {
        AttackLog log = new AttackLog();
        log.setIp(ip);
        log.setAttackType(attackType);
        log.setTimestamp(LocalDateTime.now());
        attackLogService.saveLog(log);
        return "redirect:/attacks";
    }
}