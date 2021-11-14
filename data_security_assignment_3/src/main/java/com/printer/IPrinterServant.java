package com.printer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPrinterServant extends Remote {

    void print(String password, String username,String filename, String printer) throws RemoteException;

    void queue(String password, String username, String printer) throws RemoteException;

    void topQueue(String password, String username, String printer, int job) throws RemoteException, InterruptedException;

    void start(String password, String username) throws RemoteException;

    void stop(String password, String username) throws RemoteException, InterruptedException;

    void restart(String password, String username) throws RemoteException;

    void status(String password, String username, String printer) throws RemoteException;

    void readConfig(String password, String username, String parameter) throws RemoteException;

    void setConfig(String password, String username, String parameter, String value) throws RemoteException;
    
    void initialisePrinters() throws RemoteException;
}
