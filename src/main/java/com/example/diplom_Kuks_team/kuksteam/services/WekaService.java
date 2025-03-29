package com.example.diplom_Kuks_team.kuksteam.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.SerializationHelper;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Service
public class WekaService {

    private static final String DATA_DIR = "src/main/resources/data";
    private static final String CSV_FILE_PATH = DATA_DIR + "/data.csv";
    private static final String MODEL_FILE_PATH = DATA_DIR + "/model.model";

    public String uploadAndProcessCSV(MultipartFile file) {
        try {
            String resourcePath = new File("src/main/resources/data").getAbsolutePath();
            File directory = new File(resourcePath);
            if (!directory.exists()) {
                directory.mkdirs(); // Создаём папку, если её нет
            }

            // 📄 Сохраняем CSV
            File csvFile = new File(directory, "data.csv");
            file.transferTo(csvFile);

            // 🔎 Проверяем содержимое файла
            List<String> lines = Files.readAllLines(csvFile.toPath());
            if (lines.isEmpty()) {
                return "❌ Ошибка: Загруженный CSV-файл пустой!";
            }

            return "✅ Файл успешно загружен в " + csvFile.getAbsolutePath();
        } catch (IOException e) {
            return "❌ Ошибка загрузки файла: " + e.getMessage();
        }
    }

    public String trainModel() {
        try {
            File csvFile = new File(CSV_FILE_PATH);

            // 🔹 Проверка: существует ли файл
            if (!csvFile.exists()) {
                return "❌ Ошибка: Файл data.csv не найден!";
            }

            // 🔹 Проверка: есть ли данные в файле
            if (csvFile.length() == 0) {
                return "❌ Ошибка: Файл пустой!";
            }

            // 🔹 Загружаем CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(csvFile);
            Instances dataset = loader.getDataSet();

            // 🔹 Проверка: есть ли данные после загрузки
            if (dataset.numInstances() == 0) {
                return "❌ Ошибка: В файле нет данных!";
            }

            // Устанавливаем целевой атрибут (последний столбец)
            dataset.setClassIndex(dataset.numAttributes() - 1);

            // Обучаем модель (Дерево решений J48)
            Classifier model = new J48();
            model.buildClassifier(dataset);

            // 🔹 Создаём файл модели
            File modelFile = new File(MODEL_FILE_PATH);
            SerializationHelper.write(modelFile.getAbsolutePath(), model);

            return "✅ Модель успешно обучена!";
        } catch (Exception e) {
            return "❌ Ошибка обучения модели: " + e.getMessage();
        }
    }

    public String classifyInstance(String inputData) {
        try {
            // 🔹 Загружаем модель
            File modelFile = new File(MODEL_FILE_PATH);
            if (!modelFile.exists()) {
                return "❌ Ошибка: Файл модели не найден!";
            }
            Classifier model = (Classifier) SerializationHelper.read(modelFile.getAbsolutePath());

            // 🔹 Загружаем CSV с данными
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(CSV_FILE_PATH));
            Instances dataset = loader.getDataSet();
            dataset.setClassIndex(dataset.numAttributes() - 1);

            // 🔹 Создаём новый объект для классификации
            Instances instance = new Instances(dataset, 1);
            String[] values = inputData.split(",");
            double[] instanceValues = new double[dataset.numAttributes()];

            for (int i = 0; i < values.length; i++) {
                instanceValues[i] = Double.parseDouble(values[i]);
            }

            instance.add(dataset.firstInstance().copy(instanceValues));
            double result = model.classifyInstance(instance.firstInstance());

            return "✅ Классифицированный результат: " + dataset.classAttribute().value((int) result);
        } catch (Exception e) {
            return "❌ Ошибка классификации: " + e.getMessage();
        }
    }
}
