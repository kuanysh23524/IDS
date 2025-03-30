package com.example.diplom_Kuks_team.kuksteam.controllers;

import com.example.diplom_Kuks_team.kuksteam.services.NetworkCaptureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/network")
public class NetworkCaptureController {
    @Autowired
    private final NetworkCaptureService networkCaptureService;

    public NetworkCaptureController(NetworkCaptureService networkCaptureService) {
        this.networkCaptureService = networkCaptureService;
    }

    @PostMapping ("/capture")
    public String captureTraffic(@RequestParam(name = "adapterId") String name) {
        new Thread(() -> networkCaptureService.capturePackets()).start();
        return "🚀 Захват сетевого трафика запущен!";
    }
}