//package com.example.diplom_Kuks_team.kuksteam.services;
//
//import com.opencsv.exceptions.CsvException;
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class StartupRunner {
//
//    private final CsvToDatabaseService csvToDatabaseService;
//
//    public StartupRunner(CsvToDatabaseService csvToDatabaseService) {
//        this.csvToDatabaseService = csvToDatabaseService;
//    }
//
//    @PostConstruct
//    public void runAfterStartup() throws IOException, CsvException {
//        csvToDatabaseService.importCsvToDatabase("src/main/resources/data/live_traffic.csv");
//    }
//}
