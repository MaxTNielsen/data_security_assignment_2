package com.printer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrinterServant extends UnicastRemoteObject implements IPrinterServant {
    private DB db;

    public PrinterServant() throws RemoteException {
        super();
        this.db = new DB();
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
    public Cookie start(String password) {
        //Authenticate client with password param
        return new Cookie();
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
