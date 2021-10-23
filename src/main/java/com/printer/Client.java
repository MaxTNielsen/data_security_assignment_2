package com.printer;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import com.google.gson.Gson;

public class Client {
    private static Gson gson = new Gson();

    public static void main(String args[]) throws NotBoundException, IOException {
        IPrinterServant printer = (IPrinterServant) Naming.lookup("rmi://localhost:5099/printer");
        Cookie cookie = gson.fromJson(printer.start("hello","user1"), Cookie.class);
        printer.print("file1", "printer0", gson.toJson(cookie));
        printer.print("file2", "printer1", gson.toJson(cookie));
        //printer.print("file3", "printer0", gson.toJson(cookie));
        //printer.print("file4", "printer1", gson.toJson(cookie));
        System.out.println(cookie.toString());
        System.out.println(printer.echo("Hello", gson.toJson(cookie)) + " " + printer.getClass().getName());
        //System.out.println(cookie.getId()+" "+cookie.getTimestamp());
    }
}
