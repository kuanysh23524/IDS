package com.example.diplom_Kuks_team.kuksteam.controllers;

import com.example.diplom_Kuks_team.kuksteam.repositories.TrafficRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequiredArgsConstructor
@RequestMapping("/attacks")
public class AttackController {

   private final TrafficRecordRepository trafficRecordRepository;

    @GetMapping("traffic_data")
    public String listAttacks(Model model) {

        model.addAttribute("trafficRecords", trafficRecordRepository.findAll());
        return "traffic_data";
    }

}