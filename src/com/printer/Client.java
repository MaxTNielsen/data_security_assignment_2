package com.printer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    public static void main(String args[]) throws NotBoundException, MalformedURLException, RemoteException {
        IPrinterServant printer = (IPrinterServant) Naming.lookup("rmi://localhost:5099/printer");
        System.out.println(printer.echo("Hello") + printer.getClass().getName());
    }
}
