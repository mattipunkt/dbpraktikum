package dev.numerouno.importer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public abstract class FileImporter {
    /**
     *
     */
    private File file = null;

    public FileImporter () {}

    public abstract void importFile(File file) throws IOException;

    public void filePicker() {
        JFileChooser j = new JFileChooser();

        j.showSaveDialog(null);
        this.file = j.getSelectedFile();
    }

    public File getFile() {
        return file;
    }
}
