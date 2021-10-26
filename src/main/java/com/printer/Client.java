package com.printer;

import com.google.gson.Gson;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class Client {
    private static Gson gson = new Gson();

    public static void main(String args[]) throws NotBoundException, IOException, InterruptedException {
        IPrinterServant printer = (IPrinterServant) Naming.lookup("rmi://localhost:5099/printer");
        Cookie cookie = gson.fromJson(printer.start("hello", "user1"), Cookie.class);
        printer.print("file1", "printer0", gson.toJson(cookie));
        printer.print("file2", "printer1", gson.toJson(cookie));
        printer.queue("printer0",gson.toJson(cookie));
        printer.print("file3", "printer0", gson.toJson(cookie));
        printer.topQueue("printer0", 1, gson.toJson(cookie));
        printer.queue("printer0",gson.toJson(cookie));
        printer.print("file4", "printer1", gson.toJson(cookie));
        //System.out.println(cookie.toString());
        System.out.println(printer.echo(gson.toJson(cookie)) + " " + printer.getClass().getName());
        printer.stop(gson.toJson(cookie));
        Cookie newCookie = gson.fromJson(printer.start("hello", "user1"), Cookie.class);
        printer.print("file5", "printer2", gson.toJson(newCookie));
        //System.out.println(cookie.getId()+" "+cookie.getTimestamp());
    }
}
