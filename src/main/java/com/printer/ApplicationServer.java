package com.printer;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {
    public static void main(String args[]) throws IOException {
        IDB db = new DB();
        Registry registry = LocateRegistry.createRegistry(5099);
        registry.rebind("printer", new PrinterServant(db));
    }
}