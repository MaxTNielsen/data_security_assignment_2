package com.printer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWr {
    File file;
    FileWriter writer;

    public FileWr() {
        file = new File("log.txt");
    }

    public void setWriter() {
        try {
            this.writer = new FileWriter("log.txt", true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    synchronized void writeFile(String content) {
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
