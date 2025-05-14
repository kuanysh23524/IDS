package com.example.diplom_Kuks_team.kuksteam.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Добро пожаловать в IDS Detector!");
        return "index";
    }

    @GetMapping("/contact")
    public String home() {
        return "contact";
    }


}