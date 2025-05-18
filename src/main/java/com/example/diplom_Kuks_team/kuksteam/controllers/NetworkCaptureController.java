package com.example.diplom_Kuks_team.kuksteam.controllers;

import com.example.diplom_Kuks_team.kuksteam.models.NetworkDevices;
import com.example.diplom_Kuks_team.kuksteam.repositories.NetworkDevicesRepository;
import com.example.diplom_Kuks_team.kuksteam.services.NetworkCaptureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
//@RequestMapping("/network")
public class NetworkCaptureController {
    @Autowired
    NetworkCaptureService networkCaptureService;
    @Autowired
    NetworkDevicesRepository networkDevicesRepository;


// Раньше запускался по умолчанию
//    @PostMapping("/capture")
//    public String captureTraffic(@RequestParam(name = "adapterId") String name) {
//        new Thread(() -> networkCaptureService.capturePackets()).start();
//        return "🚀 Захват сетевого трафика запущен!";
//    }

    @PostMapping("/start-capture")
    public String startCapture(@RequestParam("id") Long id) {
        NetworkDevices device = networkDevicesRepository.findById(id).orElse(null);
        if (device == null) {
            System.out.println("⚠ Устройство не найдено!");
            return "redirect:/attacks/traffic_data?error=device_not_found";
        }

        System.out.println("Выбранное устройство: " + device.getDescription());
        networkCaptureService.startCapture(device);
        return "redirect:/attacks/traffic_data";
    }

    @PostMapping("/stop-capture")
    public String stopCapture() {
        networkCaptureService.stopCapture();
        return "redirect:/attacks/traffic_data";
    }
}