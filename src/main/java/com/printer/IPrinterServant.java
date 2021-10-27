package com.printer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPrinterServant extends Remote {

    void print(String filename, String printer, String cookie) throws RemoteException;

    void queue(String printer, String cookie) throws RemoteException;

    void topQueue(String printer, int job, String cookie) throws RemoteException, InterruptedException;

    String start(String password, String username) throws RemoteException;

    void stop(String cookie) throws RemoteException, InterruptedException;

    void restart(String cookie) throws RemoteException;

    void status(String printer, String cookie) throws RemoteException;

    void readConfig(String parameter, String cookie) throws RemoteException;

    void setConfig(String parameter, String value, String cookie) throws RemoteException;

    boolean checkTimeStamp(Cookie c) throws RemoteException;

    boolean authenticateCookie(String cookie) throws RemoteException;

    void initialisePrinters() throws RemoteException;
}
