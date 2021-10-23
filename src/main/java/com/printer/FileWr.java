package com.printer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWr {
    private File file = new File("Hello1.txt");
    FileWriter writer;

    public FileWr() throws IOException {
        file.createNewFile();
        this.writer = new FileWriter(file);
    }

    public void writeFile (String content) throws IOException {
        writer.write(content);
        writer.flush();
        writer.close();
    }
}
