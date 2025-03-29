package com.example.diplom_Kuks_team.kuksteam.services;


import jakarta.annotation.PostConstruct;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class NetworkCaptureService {

    @PostConstruct
    public void startCapture() {
        capturePackets();
    }

    public void capturePackets() {
        try {
            // Получаем список всех доступных сетевых интерфейсов
            List<PcapNetworkInterface> devices = Pcaps.findAllDevs();

            if (devices.isEmpty()) {
                System.out.println("❌ Нет доступных сетевых интерфейсов.");
                return;
            }

            System.out.println("📋 Доступные интерфейсы:");
            for (int i = 0; i < devices.size(); i++) {
                System.out.println(i + ": " + devices.get(i).getName() + " - " + devices.get(i).getDescription());
            }

            // Открываем CSV-файл для записи
            FileWriter writer = new FileWriter("src/main/resources/data/live_traffic.csv");
            writer.append("src_ip,dst_ip,src_port,dst_port,protocol,bytes\n");

            // Обрабатываем каждый интерфейс
            for (PcapNetworkInterface device : devices) {
                // Пропускаем Loopback-интерфейс
                if (device.getName().contains("Loopback")) {
                    continue;
                }

                System.out.println("🔍 Пробуем использовать интерфейс: " + device.getName());

                try (PcapHandle handle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10)) {
                    System.out.println("✅ Захват начат на интерфейсе: " + device.getName());

                    while (true) {  // Бесконечный цикл для захвата всех пакетов
                        try {
                            Packet packet = handle.getNextPacketEx();

                            if (packet.contains(IpV4Packet.class)) {
                                IpV4Packet ipPacket = packet.get(IpV4Packet.class);
                                String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
                                String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
                                int length = packet.length();
                                String protocol = "OTHER";
                                int srcPort = 0, dstPort = 0;

                                if (packet.contains(TcpPacket.class)) {
                                    TcpPacket tcpPacket = packet.get(TcpPacket.class);
                                    srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
                                    dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
                                    protocol = "TCP";
                                } else if (packet.contains(UdpPacket.class)) {
                                    UdpPacket udpPacket = packet.get(UdpPacket.class);
                                    srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
                                    dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
                                    protocol = "UDP";
                                }

                                writer.append(srcIp).append(",")
                                        .append(dstIp).append(",")
                                        .append(String.valueOf(srcPort)).append(",")
                                        .append(String.valueOf(dstPort)).append(",")
                                        .append(protocol).append(",")
                                        .append(String.valueOf(length)).append("\n");

                                writer.flush();  // Записываем в файл сразу, чтобы избежать потерь
                            }
                        } catch (TimeoutException e) {
                            System.out.println("⏳ Тайм-аут при ожидании пакета...");
                        }
                    }

                } catch (PcapNativeException | NotOpenException e) {
                    System.out.println("⚠️ Ошибка при работе с " + device.getName() + ": " + e.getMessage());
                }
            }

            writer.close();
            System.out.println("✅ Данные записаны в live_traffic.csv");

        } catch (IOException e) {
            System.out.println("❌ Ошибка при записи в файл: " + e.getMessage());
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }
}
