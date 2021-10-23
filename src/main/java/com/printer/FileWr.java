package com.printer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWr {
    File file = new File("Hello1.txt");
    FileWriter writer;

    public FileWr() {
        try {
            file.createNewFile();
            this.writer = new FileWriter(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    synchronized void writeFile(String content){
        try {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void cleanUp() {
        try {
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
