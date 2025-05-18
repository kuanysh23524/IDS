package com.example.diplom_Kuks_team.kuksteam.controllers;

import com.example.diplom_Kuks_team.kuksteam.models.NetworkDevices;
import com.example.diplom_Kuks_team.kuksteam.repositories.NetworkDevicesRepository;
import com.example.diplom_Kuks_team.kuksteam.services.NetworkCaptureService;
import com.example.diplom_Kuks_team.kuksteam.services.SearchNetworkDevicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
//@RequestMapping("/network")
public class NetworkCaptureController {
    @Autowired
    NetworkCaptureService networkCaptureService;
    SearchNetworkDevicesService searchNetworkDevicesService;


// Раньше запускался по умолчанию
//    @PostMapping("/capture")
//    public String captureTraffic(@RequestParam(name = "adapterId") String name) {
//        new Thread(() -> networkCaptureService.capturePackets()).start();
//        return "🚀 Захват сетевого трафика запущен!";
//    }

    @PostMapping("/start-capture")
    public String startCapture(NetworkDevices device) {
//        searchNetworkDevicesService.chooseDevice(device);

        System.out.println("ID c фронта  " + device.getDescription());
        networkCaptureService.startCapture(device);
        return "redirect:/attacks/traffic_data";
    }

    @PostMapping("/stop-capture")
    public String stopCapture() {
        networkCaptureService.stopCapture();
        return "redirect:/attacks/traffic_data";
    }
}