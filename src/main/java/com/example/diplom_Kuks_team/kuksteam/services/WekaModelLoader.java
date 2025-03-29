package com.example.diplom_Kuks_team.kuksteam.services;

import weka.classifiers.trees.J48;
import weka.core.SerializationHelper;

public class WekaModelLoader {
    public static void main(String[] args) {
        try {
            String modelPath = "C:\\Users\\kukao\\ideaProjects\\kuksteam\\src\\main\\resources\\data\\model.model"; // укажи свой путь
            J48 tree = (J48) SerializationHelper.read(modelPath);
            System.out.println(tree.toString()); // Вывод структуры дерева
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}