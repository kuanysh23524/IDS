package com.example.diplom_Kuks_team.kuksteam.controllers;

import com.example.diplom_Kuks_team.kuksteam.services.NetworkCaptureService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/network")
public class NetworkCaptureController {
    private final NetworkCaptureService networkCaptureService;

    public NetworkCaptureController(NetworkCaptureService networkCaptureService) {
        this.networkCaptureService = networkCaptureService;
    }

    @GetMapping("/capture")
    public String captureTraffic() {
        new Thread(() -> networkCaptureService.capturePackets()).start();
        return "🚀 Захват сетевого трафика запущен!";
    }
}