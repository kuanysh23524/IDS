package com.example.diplom_Kuks_team.kuksteam.controllers;


import com.example.diplom_Kuks_team.kuksteam.services.WekaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/weka")
public class WekaController {

    private final WekaService wekaService;

    public WekaController(WekaService wekaService) {
        this.wekaService = wekaService;
    }

    /**
     * Страница обучения модели
     */
    @GetMapping("/train")
    public String trainPage() {
        return "train";
    }

    /**
     * Запуск обучения модели
     */
    @PostMapping("/train")
    public String trainModel(Model model) {
        String message = wekaService.trainModel();
        model.addAttribute("message", message);
        return "train";
    }

    /**
     * Страница классификации данных
     */
    @GetMapping("/classify")
    public String classifyPage() {
        return "classify";
    }

    /**
     * Обработка запроса классификации
     */
    @PostMapping("/classify")
    public String classify(@RequestParam("input") String inputData, Model model) {
        String result = wekaService.classifyInstance(inputData);
        model.addAttribute("result", result);
        return "classify";
    }
}