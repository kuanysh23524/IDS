package com.example.diplom_Kuks_team.kuksteam.services;

import com.example.diplom_Kuks_team.kuksteam.models.NetworkDevices;
import com.example.diplom_Kuks_team.kuksteam.repositories.NetworkDevicesRepository;
import jakarta.annotation.PostConstruct;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SearchNetworkDevicesService {
    @Autowired
    NetworkDevicesRepository networkDevicesRepository;

    @PostConstruct
    public void saveNetworkDevices() throws PcapNativeException {
        List<PcapNetworkInterface> networkInterfaces = Pcaps.findAllDevs();
        for (PcapNetworkInterface networkInterface : networkInterfaces) {
            // Проверяем, есть ли уже такой интерфейс в базе
            Optional<NetworkDevices> existingDevice = networkDevicesRepository.findByName(networkInterface.getName());

            if (existingDevice.isEmpty()) {
                NetworkDevices networkDevices = new NetworkDevices();
                networkDevices.setName(networkInterface.getName());
                networkDevices.setDescription(networkInterface.getDescription());
                networkDevicesRepository.save(networkDevices);
            }
        }
    }

}
