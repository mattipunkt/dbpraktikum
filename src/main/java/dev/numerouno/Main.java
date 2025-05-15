package dev.numerouno;


import dev.numerouno.importer.XmlImporter;

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
    }
}