package dev.numerouno;


import dev.numerouno.db.Database;
import dev.numerouno.importer.XmlImporter;
import org.apache.commons.lang3.time.StopWatch;
import java.io.File;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Database database = new Database();
        XmlImporter importer = new XmlImporter(database);
        // importer.filePicker();
        importer.setFile(new File("/home/matti/Dokumente/dbpraktikum/presets/leipzig_transformed.xml"));
        System.out.println(importer.getFile().getAbsolutePath());
        try {
            importer.parseXml();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        database.close();
        stopWatch.stop();
        System.out.println("Time elapsed:");
        System.out.println(stopWatch.getTime(TimeUnit.SECONDS));


        // Database database1 = new Database();
        // CsvImporter csvImporter = new CsvImporter(database1);
        // try {
        //     List<Review> reviews = csvImporter.parseReviews("presets/reviews.csv");
        //     csvImporter.saveReviewsToDatabase(reviews, database1);
        // } catch (Exception e) {
        //     throw new RuntimeException(e);
        // }
    }
}