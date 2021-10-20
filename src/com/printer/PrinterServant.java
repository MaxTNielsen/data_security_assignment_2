package com.printer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrinterServant extends UnicastRemoteObject implements IPrinterServant {

    public PrinterServant() throws RemoteException {
        super();
    }

    //Test method
    @Override
    public String echo(String s) throws RemoteException{
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
    public void start(String password) {
        throw new UnsupportedOperationException("");
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
