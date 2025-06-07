package com.example.diplom_Kuks_team.kuksteam.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;

import java.io.File;

@Service
public class WekaService {

    private static final String DATA_DIR = "src/main/resources/data";
    private static final String MODEL_FILE_PATH = DATA_DIR + "/model.model";
    private static final String LIVE_TFAFFIC_FOR_TRAINING = "src/main/resources/data/live_traffic.csv";

    @PostConstruct
    public String trainModel() {
        try {
            File csvFile = new File(LIVE_TFAFFIC_FOR_TRAINING);

            if (!csvFile.exists()) {
                return "❌ Ошибка: Файл data.csv не найден!";
            }

            if (csvFile.length() == 0) {
                return "❌ Ошибка: Файл пустой!";
            }

            CSVLoader loader = new CSVLoader();
            loader.setSource(csvFile);
            Instances dataset = loader.getDataSet();

            /**
             Проверка: есть ли данные после загрузки
             **/
            if (dataset.numInstances() == 0) {
                return "❌ Ошибка: В файле нет данных!";
            }

            /**
             Устанавливаем целевой атрибут (последний столбец)
             **/
            dataset.setClassIndex(dataset.numAttributes() - 1);

            /**
             Обучаем модель
             **/
            Classifier model = new J48();
            model.buildClassifier(dataset);

            /**
             Создаём файл модели
             **/
            File modelFile = new File(MODEL_FILE_PATH);
            SerializationHelper.write(modelFile.getAbsolutePath(), model);

            return "✅ Модель успешно обучена!";
        } catch (Exception e) {
            return "❌ Ошибка обучения модели: " + e.getMessage();
        }
    }

    public String classifyInstance(String inputData) {
        try {

            /**
             Загружаем модель
             **/
            File modelFile = new File(MODEL_FILE_PATH);
            if (!modelFile.exists()) {
                return "❌ Ошибка: Файл модели не найден!";
            }

            /**
             Загрузка модели с использованием Weka SerializationHelper
             **/
            Classifier model = (Classifier) SerializationHelper.read(modelFile.getAbsolutePath());

            /**
             Загружаем CSV с данными
             **/
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(LIVE_TFAFFIC_FOR_TRAINING));
            Instances dataset = loader.getDataSet();
            dataset.setClassIndex(dataset.numAttributes() - 1); // Устанавливаем последний атрибут как класс

            /**
             Разбираем входные данные
             **/
            String[] values = inputData.split(",");
            if (values.length != dataset.numAttributes() - 1) {
                return "❌ Ошибка: Неверное количество параметров. Ожидалось " + (dataset.numAttributes() - 1);
            }

            /**
             Преобразуем входные данные в числовые значения
             **/
            double[] instanceValues = new double[dataset.numAttributes()];
            for (int i = 0; i < values.length; i++) { // Обрабатываем все атрибуты
                if (dataset.attribute(i).isNumeric()) {
                    // Если атрибут числовой, то парсим его как число
                    instanceValues[i] = Double.parseDouble(values[i]);
                } else {
                    // Если атрибут категориальный, то получаем индекс значения
                    instanceValues[i] = dataset.attribute(i).indexOfValue(values[i]);
                }
            }

            /**
             Создаём новый объект для классификации
             **/
            Instance newInstance = new DenseInstance(1.0, instanceValues);
            newInstance.setDataset(dataset); // Указываем, что он относится к датасету

            /**
             Классифицируем новый объект
             **/
            double result = model.classifyInstance(newInstance);
            String predictedClass = dataset.classAttribute().value((int) result);

            return "✅ Классифицированный результат: " + predictedClass;
        } catch (Exception e) {
            return "❌ Ошибка классификации: " + e.getMessage();
        }
    }
}
