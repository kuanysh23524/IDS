//package com.example.diplom_Kuks_team.kuksteam.services;
//
//import com.example.diplom_Kuks_team.kuksteam.models.TrafficRecord;
//import com.example.diplom_Kuks_team.kuksteam.repositories.TrafficRecordRepository;
//import com.opencsv.CSVReader;
//import com.opencsv.exceptions.CsvException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class CsvToDatabaseService {
//    @Autowired
//    TrafficRecordRepository trafficRecordRepository;
//
//    public void importCsvToDatabase(String filePath) {
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            boolean isFirstLine = true;
//
//            while ((line = br.readLine()) != null) {
//                if (isFirstLine) {  // Пропускаем заголовок
//                    isFirstLine = false;
//                    continue;
//                }
//
//                String[] fields = line.split(",");
//                if (fields.length < 6) {
//                    System.out.println("⚠ Ошибка: некорректная строка - " + line);
//                    continue;
//                }
//
//                String srcIp = fields[0];
//                String dstIp = fields[1];
//                int srcPort = Integer.parseInt(fields[2]);
//                int dstPort = Integer.parseInt(fields[3]);
//                String protocol = fields[4];
//                int bytes = Integer.parseInt(fields[5]);
//
//                System.out.println("✅ Загружена запись: " + srcIp + " → " + dstIp);
//
//                // Здесь должен быть вызов метода для сохранения в БД
//                saveToDatabase(srcIp, dstIp, srcPort, dstPort, protocol, bytes);
//            }
//        } catch (IOException | NumberFormatException e) {
//            System.out.println("❌ Ошибка при обработке CSV: " + e.getMessage());
//        }
//    }
//
//    private void saveToDatabase(String srcIp, String dstIp, int srcPort, int dstPort, String protocol, int bytes) {
//        // Реализация сохранения в базу данных через JPA-репозиторий
//        TrafficRecord record = new TrafficRecord(srcIp , dstIp ,srcPort , dstPort , protocol , bytes);
//        trafficRecordRepository.save(record);
//    }
//}
