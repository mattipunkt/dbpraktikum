package dev.numerouno.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntegrityLogger {
    private List<String> problematicProducts = new ArrayList<>();
    private String problem;

    public enum ErrorType {
        SYNTAX_ERROR,
        DUPLICATE_ENTRY,
        TYPE_CONVERSION,
        NULL_VALUE,
        INVALID_DATA, MISSING_DATA, UNKNOWN_ERROR, DB_ERROR, INTEGRITY_CONFLICT
    }
    private HashMap<ErrorType, Integer> errors = new HashMap<>();

    public IntegrityLogger() {
    }

    public void addError(ErrorType et, String problem) {
        problematicProducts.add(problem);
        errors.put(et, errors.getOrDefault(et, 0) + 1);
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
                for(String problem : problematicProducts) {
                    fw.append(problem);
                    fw.append("\n");
                }
                fw.append("\n");
                fw.append("====================\nSummary:\n");
                for (ErrorType et : errors.keySet()) {
                    fw.append(et.toString() + ": " + errors.get(et) + "\n");
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create log file!", e);
        }
    }
}
