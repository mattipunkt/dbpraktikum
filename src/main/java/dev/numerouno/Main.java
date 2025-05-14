package dev.numerouno;


import dev.numerouno.importer.FileImporter;

public class Main {
    public static void main(String[] args) {
        FileImporter importer = new FileImporter();
        importer.filePicker();
        System.out.println(importer.getFile().getAbsolutePath());
    }
}