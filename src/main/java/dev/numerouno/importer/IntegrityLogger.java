package dev.numerouno.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IntegrityLogger {
    private List<Product> problematicProducts = new ArrayList<>();
    private String problem;

    public IntegrityLogger() {
    }

    public void addProduct(String problem, Product product) {
        problematicProducts.add(product);
        this.problem = problem;
    }

    public int countProblems() {
        return problematicProducts.size();
    }

    public void printProblemsToFile(File file) {
        try {
            if (file.createNewFile()) {
                System.out.println("Created new log-file: " + file.getAbsolutePath());
            }
            try (FileWriter fw = new FileWriter(file)) {
                for(Product problem : problematicProducts) {
                    fw.append(problem.toString());
                    fw.append("\n");
                }
                fw.append("\n");
                fw.append("====================\nSummary:");
                fw.append("Integrity Errors: ").append(String.valueOf(countProblems())).append("\n");

            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create log file!", e);
        }
    }
}
