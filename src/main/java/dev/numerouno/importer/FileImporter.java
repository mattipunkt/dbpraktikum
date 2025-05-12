package dev.numerouno.importer;

import java.io.File;
import java.io.IOException;

public interface FileImporter {

    void importFile(File file) throws IOException;
}
