package dev.numerouno.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Die Klasse {@code IntegrityLogger} dient zur Protokollierung und Analyse von Datenintegritätsproblemen,
 * die während eines Importvorgangs oder einer Verarbeitung auftreten können.
 *
 * Sie speichert aufgetretene Fehler in einer Liste sowie deren Häufigkeit nach Typ.
 * Zusätzlich können die protokollierten Probleme in eine Datei ausgegeben werden.
 */
public class IntegrityLogger {
    private List<String> problematicProducts = new ArrayList<>();
    private String problem;

    /**
     * Enum zur Klassifikation der verschiedenen Fehlertypen.
     */
    public enum ErrorType {
        SYNTAX_ERROR,
        DUPLICATE_ENTRY,
        TYPE_CONVERSION,
        NULL_VALUE,
        INVALID_DATA, MISSING_DATA, UNKNOWN_ERROR, DB_ERROR, INTEGRITY_CONFLICT
    }
    private HashMap<ErrorType, Integer> errors = new HashMap<>();

    /**
     * Konstruktor zur Initialisierung eines neuen {@code IntegrityLogger}.
     */
    public IntegrityLogger() {
    }

    /**
     * Fügt einen Fehler hinzu und protokolliert die zugehörige Beschreibung.
     *
     * @param et Der Typ des Fehlers.
     * @param problem Die Beschreibung des Problems.
     */
    public void addError(ErrorType et, String problem) {
        problematicProducts.add(problem);
        errors.put(et, errors.getOrDefault(et, 0) + 1);
    }

    /**
     * Gibt die Gesamtzahl der aufgezeichneten Probleme zurück.
     *
     * @return Anzahl der protokollierten Probleme.
     */
    public int countProblems() {
        return problematicProducts.size();
    }


    /**
     * Schreibt alle protokollierten Probleme und eine Zusammenfassung der Fehler
     * in die angegebene Datei.
     *
     * @param file Die Datei, in die das Protokoll geschrieben wird.
     * @throws RuntimeException wenn die Datei nicht erstellt oder beschrieben werden kann.
     */
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
