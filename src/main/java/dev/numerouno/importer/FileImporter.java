package dev.numerouno.importer;

import dev.numerouno.db.Database;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Abstrakte Basisklasse für Datei-Importer.
 * <p>
 * Diese Klasse stellt die grundlegende Struktur für Importer bereit,
 * die Dateien einlesen und in eine Datenbank importieren.
 * </p>
 */
public abstract class FileImporter {

    private File file = null;
    protected Database database = null;

    /**
     * Konstruktor, der eine Datenbankinstanz übergibt.
     *
     * @param db Die Datenbank, in die importiert werden soll.
     */
    public FileImporter (Database db) {
        this.database = db;
    }

    /**
     * Abstrakte Methode zum Importieren einer Datei.
     * Diese Methode muss von Unterklassen implementiert werden,
     * um eine spezifische Importlogik bereitzustellen.
     *
     * @param file Die Datei, die importiert werden soll.
     * @throws IOException Wenn ein Fehler beim Einlesen der Datei auftritt.
     */
    public abstract void importFile(File file) throws IOException;

    /**
     * Öffnet einen Datei-Dialog, mit dem der Benutzer eine Datei auswählen kann.
     * Die ausgewählte Datei wird intern gespeichert.
     *
     * @param text Der Titel des Datei-Dialogs.
     */
    public void filePicker(String text) {
        JFileChooser j = new JFileChooser();
        j.setDialogTitle(text);

        j.showSaveDialog(null);
        this.file = j.getSelectedFile();
    }

    /**
     * Gibt die aktuell gespeicherte Datei zurück.
     *
     * @return Die ausgewählte Datei, oder {@code null}, wenn keine Datei ausgewählt wurde.
     */
    public File getFile() {
        return file;
    }

    /**
     * Setzt die aktuell zu importierende Datei.
     *
     * @param file Die Datei, die gesetzt werden soll.
     */
    public void setFile(File file) {
        this.file = file;
    }

}
