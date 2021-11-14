package com.printer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWr {
    File file;
    FileWriter writer;

    public FileWr() {
        file = new File("data_security_assignment_3/proto_2/log.txt");
    }

    public void setWriter() {
        try {
            this.writer = new FileWriter("data_security_assignment_3/proto_2/log.txt", true);
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
