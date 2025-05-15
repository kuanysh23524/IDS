package com.example.diplom_Kuks_team.kuksteam.services;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaClassifier {
    public static void main(String[] args) throws Exception {
        // Загрузка модели
        Classifier cls = (Classifier) weka.core.SerializationHelper.read("j48_model.model");

        // Загрузка тестового набора (CSV или ARFF)
        DataSource source = new DataSource("test_data.arff"); // Или CSV
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        // Классификация каждого экземпляра
        for (int i = 0; i < data.numInstances(); i++) {
            Instance inst = data.instance(i);
            double label = cls.classifyInstance(inst);
            System.out.println("Instance " + i + " -> Class: " + data.classAttribute().value((int) label));
        }
        System.out.println("detached");
    }

}

