package com.printer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import com.google.gson.Gson;

public class PrinterServant extends UnicastRemoteObject implements IPrinterServant {
    private IDB db;
    private Gson gson = new Gson();

    public PrinterServant(IDB db) throws RemoteException {
        super();
        this.db = db;
    }

    //Test method
    @Override
    public String echo(String s) throws RemoteException {
        return "From Server" + s;
    }

    @Override
    public void print(String filename, String parameter) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void queue(String printer) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void topQueue(String printer, int job) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public String start(String password, String username) {
        //Authenticate client with password param
        if (db.authenticateUser(password, username))
            System.out.println("it worked!");
        else{
            System.out.println("it didnt work");
        }
        return gson.toJson(new Cookie());
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void restart() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void status(String printer) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void readConfig(String parameter) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void setConfig(String parameter, String value) {
        throw new UnsupportedOperationException("");
    }
}
