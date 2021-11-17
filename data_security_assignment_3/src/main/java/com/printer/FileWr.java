package com.printer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWr {
    File file;
    FileWriter writer;

    public FileWr() {
        file = new File("C:/Users/tuetr_d2rngny/git_repos/data_security_repos/data_security_assignment_3/proto_1/log.txt");
        System.out.println("printing in filewriter constructor");
    }

    public void setWriter() {
        try {
            this.writer = new FileWriter("C:/Users/tuetr_d2rngny/git_repos/data_security_repos/data_security_assignment_3/proto_1/log.txt", true);
            System.out.println("printing in filewriter setWriter");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    synchronized void writeFile(String content) {
        try {
            writer.write(content);
            System.out.println("printing" + content + "in filewriter writeFile");
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
