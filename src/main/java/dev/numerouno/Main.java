package dev.numerouno;


import dev.numerouno.db.Database;
import dev.numerouno.importer.CsvImporter;
import dev.numerouno.importer.Review;
import dev.numerouno.importer.XmlImporter;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        XmlImporter importer = new XmlImporter();
        importer.filePicker();
        System.out.println(importer.getFile().getAbsolutePath());
        try {
            importer.parseXml();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Database database = new Database();

        CsvImporter csvImporter = new CsvImporter();
        try {
            List<Review> reviews = csvImporter.parseReviews("presets/reviews.csv");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}