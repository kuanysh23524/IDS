package com.example.diplom_Kuks_team.kuksteam.services;

import com.example.diplom_Kuks_team.kuksteam.enums.AttackTypes;
import com.example.diplom_Kuks_team.kuksteam.models.NetworkDevices;
import com.example.diplom_Kuks_team.kuksteam.models.TrafficRecord;
import com.example.diplom_Kuks_team.kuksteam.repositories.TrafficRecordRepository;
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
import java.util.ArrayList;
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
    SearchNetworkDevicesService searchNetworkDevicesService;


//    @PostConstruct
//    public void startCapture() {
//        CompletableFuture.runAsync(this::capturePackets);
//    }


    private volatile boolean capturing = false;

    public void startCapture(NetworkDevices networkDevices) {
        capturing = true;
        if (capturing) {
            CompletableFuture.runAsync(() -> {
                capturePackets(networkDevices);
            });
        }
    }

    public void stopCapture() {
        capturing = false;

    }

    public void capturePackets(NetworkDevices deviceToChoose) {
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
//                if (device.getDescription().contains("MediaTek Wi-Fi 6 MT7921 Wireless LAN Card"))
                if (device.getDescription().contains(String.valueOf(deviceToChoose.getDescription()))) {


                    System.out.println("🔍 Пробуем использовать интерфейс: " + device.getName());

                    try (PcapHandle handle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10)) {
                        System.out.println("✅ Захват начат на интерфейсе: " + device.getName());

                        long startTime = System.currentTimeMillis();

                        while (capturing && System.currentTimeMillis() - startTime < CAPTURE_DURATION * 1000) {
                            try {
                                Packet packet = handle.getNextPacketEx();
                                processPacket(packet, writer, deviceToChoose);
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


    // Method scanning attacks--------------------------------------------------------------------------

    private static final int PORT_SCAN_THRESHOLD = 7;
    private static final int PACKET_SIZE_THRESHOLD = 1400;

    private static final int DDOS_REQUEST_COUNT = 5;
    private static final int DDOS_INTERVAL_MS = 1000;

    private static final int DDOS_REQUEST_COUNT_TOTAL = 200;
    private static final int DDOS_LONG_INTERVAL_MS = 3000;
    private static final int DDOS_UNIQUE_IP_THRESHOLD = 30;

    // Для Port Scan
//    private final Map<String, Integer> requestCounter = new HashMap<>();

    // Для простого DDoS (от одного IP)
    private final Map<String, List<Long>> recentRequests = new HashMap<>();

    // Для продвинутого DDoS (по dstIp)
    private final Map<String, List<Long>> incomingTrafficTimestamps = new HashMap<>();
    private final Map<String, Map<String, List<Long>>> ddosMap = new HashMap<>();
    // Типы атак

    private void processPacket(Packet packet, FileWriter writer, NetworkDevices networkDevices) throws IOException {
        if (!packet.contains(IpV4Packet.class)) return;

        IpV4Packet ipPacket = packet.get(IpV4Packet.class);
        String srcIp = ipPacket.getHeader().getSrcAddr().getHostAddress();
        String dstIp = ipPacket.getHeader().getDstAddr().getHostAddress();
        int length = packet.length();
        String protocol = "OTHER";
        int srcPort = 0, dstPort = 0;
        String attackType = AttackTypes.NORMAL.name();

        // Протоколы и порты
        if (packet.contains(TcpPacket.class)) {
            TcpPacket tcpPacket = packet.get(TcpPacket.class);
            srcPort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = tcpPacket.getHeader().getDstPort().valueAsInt();
            protocol = "TCP";

            // SYN Flood
            if (tcpPacket.getHeader().getSyn() && !tcpPacket.getHeader().getAck()) {
                attackType = AttackTypes.SYN_FLOOD.name();
            }

            // NULL Packet
            if (!tcpPacket.getHeader().getSyn() &&
                    !tcpPacket.getHeader().getAck() &&
                    !tcpPacket.getHeader().getFin() &&
                    !tcpPacket.getHeader().getRst() &&
                    !tcpPacket.getHeader().getPsh() &&
                    !tcpPacket.getHeader().getUrg()) {
                attackType = AttackTypes.NULL_PACKET.name();
            }

        } else if (packet.contains(UdpPacket.class)) {
            UdpPacket udpPacket = packet.get(UdpPacket.class);
            srcPort = udpPacket.getHeader().getSrcPort().valueAsInt();
            dstPort = udpPacket.getHeader().getDstPort().valueAsInt();
            protocol = "UDP";
        }

        // Port Scan Detection
        String ipPortKey = srcIp + ":" + dstPort;
        requestCounter.put(ipPortKey, requestCounter.getOrDefault(ipPortKey, 0) + 1);
        if (requestCounter.get(ipPortKey) > PORT_SCAN_THRESHOLD) {
            attackType = AttackTypes.PORT_SCAN.name();
        }

        // DDoS / Brute-force Detection от одного IP (локального)
        if (srcIp.startsWith("10.")) {
            long currentTime = System.currentTimeMillis();
            List<Long> times = recentRequests.getOrDefault(srcIp, new ArrayList<>());
            times.add(currentTime);
            times.removeIf(t -> currentTime - t > DDOS_INTERVAL_MS);
            recentRequests.put(srcIp, times);

            if (times.size() >= DDOS_REQUEST_COUNT) {
                attackType = AttackTypes.DDOS_OR_BRUTE_FORCE.name();
            }
        }

        // Malformed Packet Detection
        if (length > PACKET_SIZE_THRESHOLD && !ipPacket.getHeader().getMoreFragmentFlag()) {
            attackType = AttackTypes.MALFORMED_PACKET.name();
        }

        // === Улучшенное DDoS Detection ===
        long currentTime = System.currentTimeMillis();

        // Подсчет общего количества пакетов на dstIp за интервал
        List<Long> timestamps = incomingTrafficTimestamps.getOrDefault(dstIp, new ArrayList<>());
        timestamps.add(currentTime);
        timestamps.removeIf(t -> currentTime - t > DDOS_LONG_INTERVAL_MS);
        incomingTrafficTimestamps.put(dstIp, timestamps);

        if (timestamps.size() >= DDOS_REQUEST_COUNT_TOTAL) {
            attackType = AttackTypes.DDOS_OR_BRUTE_FORCE.name();
        }

        // Подсчет количества уникальных атакующих IP для одного dstIp
        Map<String, List<Long>> srcMap = ddosMap.getOrDefault(dstIp, new HashMap<>());
        List<Long> timeList = srcMap.getOrDefault(srcIp, new ArrayList<>());
        timeList.add(currentTime);
        timeList.removeIf(t -> currentTime - t > DDOS_LONG_INTERVAL_MS);
        srcMap.put(srcIp, timeList);
        ddosMap.put(dstIp, srcMap);

        long activeAttackers = srcMap.values().stream()
                .filter(list -> list.size() > 1)
                .count();

        if (activeAttackers >= DDOS_UNIQUE_IP_THRESHOLD) {
            attackType = AttackTypes.DDOS_FROM_MULTIPLE_SOURCES.name();
        }

        // Запись в базу данных
        TrafficRecord record = new TrafficRecord(
                null, srcIp, dstIp, srcPort, dstPort, protocol, length, attackType, LocalDateTime.now(), networkDevices
        );
        trafficRecordRepository.save(record);


        // Запись в CSV
        writer.append(String.join(",", srcIp, dstIp,
                        String.valueOf(srcPort), String.valueOf(dstPort),
                        protocol, String.valueOf(length), attackType))
                .append("\n");
        writer.flush();
    }


}