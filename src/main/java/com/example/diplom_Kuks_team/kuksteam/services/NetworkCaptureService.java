package com.example.diplom_Kuks_team.kuksteam.services;

import com.example.diplom_Kuks_team.kuksteam.models.TrafficRecord;
import com.example.diplom_Kuks_team.kuksteam.repositories.NetworkDevicesRepository;
import com.example.diplom_Kuks_team.kuksteam.repositories.TrafficRecordRepository;
import jakarta.annotation.PostConstruct;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;
import org.pcap4j.packet.UdpPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
public class NetworkCaptureService {

    private static final int CAPTURE_DURATION = 900; // Время захвата в секундах
    private static final Map<String, Integer> requestCounter = new HashMap<>();
    private static final Map<String, Long> lastRequestTime = new HashMap<>();
    @Autowired
    TrafficRecordRepository trafficRecordRepository;
    NetworkDevicesRepository networkDevicesRepository;

    @PostConstruct
    public void startCapture() {
        CompletableFuture.runAsync(this::capturePackets);
    }

    public void capturePackets() {
        try {
            List<PcapNetworkInterface> devices = Pcaps.findAllDevs();

            if (devices.isEmpty()) {
                System.out.println("❌ Нет доступных сетевых интерфейсов.");
                return;
            }

            System.out.println("📋 Доступные интерфейсы:");
            for (int i = 0; i < devices.size(); i++) {
                System.out.println(i + ": " + devices.get(i).getName() + " - " + devices.get(i).getDescription());
            }
//            Optional<NetworkDevices> networkDevices = networkDevicesRepository.findByName(name);
//            System.out.println(networkDevices.isPresent());

            FileWriter writer = new FileWriter("src/main/resources/data/live_traffic.csv", true);
//            writer.append("src_ip,dst_ip,src_port,dst_port,protocol,bytes,attack_type\n");

            for (PcapNetworkInterface device : devices) {
                if (device.getName().contains("Loopback")) {
                    continue;
                }

//                Optional<NetworkDevices> networkDevices = networkDevicesRepository.findByName(name);
//
                if (device.getDescription().contains("MediaTek Wi-Fi 6 MT7921 Wireless LAN Card"))
//                if (device.getDescription().equals(networkDevices.get().getDescription()))
                {


                    System.out.println("🔍 Пробуем использовать интерфейс: " + device.getName());

                    try (PcapHandle handle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10)) {
                        System.out.println("✅ Захват начат на интерфейсе: " + device.getName());

                        long startTime = System.currentTimeMillis();

                        while (System.currentTimeMillis() - startTime < CAPTURE_DURATION * 1000) {
                            try {
                                Packet packet = handle.getNextPacketEx();
                                processPacket(packet, writer);
                            } catch (TimeoutException e) {
                                System.out.println("⏳ Тайм-аут при ожидании пакета...");
                            }
                        }

                    } catch (PcapNativeException | NotOpenException e) {
                        System.out.println("⚠️ Ошибка при работе с " + device.getName() + ": " + e.getMessage());
                    }
                }

            }


            writer.close();
            System.out.println("✅ Данные записаны в live_traffic.csv");

        } catch (IOException | PcapNativeException e) {
            System.out.println("❌ Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private void processPacket(Packet packet, FileWriter writer) throws IOException {
        if (packet.contains(IpV4Packet.class)) {


//            List<TrafficRecord> trafficRecords = new ArrayList<>();
            IpV4Packet ipPacket = packet.get(IpV4Packet.class);
            String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
            String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
            int length = packet.length();
            String protocol = "OTHER";
            int srcPort = 0, dstPort = 0;
            String attackType = "NORMAL";

            if (packet.contains(TcpPacket.class)) {
                TcpPacket tcpPacket = packet.get(TcpPacket.class);
                srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
                dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
                protocol = "TCP";

                // SYN Flood Detection
                if (tcpPacket.getHeader().getSyn() && !tcpPacket.getHeader().getAck()) {
                    attackType = "SYN_FLOOD";
                }
            } else if (packet.contains(UdpPacket.class)) {
                UdpPacket udpPacket = packet.get(UdpPacket.class);
                srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
                dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
                protocol = "UDP";
            }

            // Port Scanning Detection (Multiple requests from same IP to different ports)
            String ipPortKey = srcIp + ":" + dstPort;
            requestCounter.put(ipPortKey, requestCounter.getOrDefault(ipPortKey, 0) + 1);
            if (requestCounter.get(ipPortKey) > 10) {
                attackType = "PORT_SCAN";
            }

            // DDoS / Brute-force Detection (High-frequency requests from same IP)
            long currentTime = System.currentTimeMillis();
            if (lastRequestTime.containsKey(srcIp) && (currentTime - lastRequestTime.get(srcIp)) < 100) {
                attackType = "DDOS_OR_BRUTE_FORCE";
            }
            lastRequestTime.put(srcIp, currentTime);

            // Unusual Packet Size Detection (Common for some exploits)
            if (length > 1500) {
                attackType = "MALFORMED_PACKET";
            }
            // 🚀 Записываем данные во временный список
            TrafficRecord record = new TrafficRecord(null, srcIp, dstIp, srcPort, dstPort, protocol, length, attackType, LocalDateTime.now());
            trafficRecordRepository.save(record);

            // Write to CSV
            writer.append(srcIp).append(",")
                    .append(dstIp).append(",")
                    .append(String.valueOf(srcPort)).append(",")
                    .append(String.valueOf(dstPort)).append(",")
                    .append(protocol).append(",")
                    .append(String.valueOf(length)).append(",")
                    .append(attackType).append("\n");

            writer.flush();
        }
    }


}