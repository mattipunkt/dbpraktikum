package dev.numerouno.importer;

import dev.numerouno.db.Database;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public abstract class FileImporter {
    /**
     *
     */
    private File file = null;
    protected Database database = null;

    public FileImporter (Database db) {
        this.database = db;
    }

    public abstract void importFile(File file) throws IOException;

    public void filePicker(String text) {
        JFileChooser j = new JFileChooser();
        j.setDialogTitle(text);

        j.showSaveDialog(null);
        this.file = j.getSelectedFile();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
