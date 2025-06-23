package dev.numerouno;


import dev.numerodos.Queries;
import dev.numerouno.db.Database;
import dev.numerouno.importer.CsvImporter;
import dev.numerouno.importer.Review;
import dev.numerouno.importer.XmlImporter;
import org.apache.commons.lang3.time.StopWatch;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Database database = new Database();



//        // XML Import
//        XmlImporter importer = new XmlImporter(database);
//        // importer.filePicker("Kategorie auswählen");
//        importer.setFile(new File("presets/categories.xml"));
//        System.out.println("Importiere: " + importer.getFile().getAbsolutePath());
//        try {
//            importer.parseXml();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        importer.setFile(new File("presets/dresden.xml"));
//        System.out.println("Importiere: " + importer.getFile().getAbsolutePath());
//        try {
//            importer.parseXml();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        importer.setFile(new File("presets/leipzig_transformed.xml"));
//        System.out.println("Importiere: " + importer.getFile().getAbsolutePath());
//        try {
//            importer.parseXml();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }





//        // CSV Import
//        CsvImporter csvImporter = new CsvImporter(database);
//        try {
//            List<Review> reviews = csvImporter.parseReviews("presets/reviews.csv");
//            csvImporter.saveReviewsToDatabase(reviews, database);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        try {
            Queries q = new Queries(database);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        database.close();
        stopWatch.stop();
        System.out.println("Time elapsed:");
        System.out.println(stopWatch.getTime(TimeUnit.SECONDS));

    }
}