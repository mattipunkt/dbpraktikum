package dev.numerouno;


import dev.numerouno.db.Database;
import dev.numerouno.importer.CsvImporter;
import dev.numerouno.importer.Review;
import dev.numerouno.importer.XmlImporter;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Database database = new Database();
        XmlImporter importer = new XmlImporter(database);
        // importer.filePicker();
        importer.setFile(new File("/home/matti/Dokumente/dbpraktikum/presets/categories.xml"));
        System.out.println(importer.getFile().getAbsolutePath());
        try {
            importer.parseXml();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        database.close();


        /**
        CsvImporter csvImporter = new CsvImporter();
        try {
            List<Review> reviews = csvImporter.parseReviews("presets/reviews.csv");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
         **/
    }
}