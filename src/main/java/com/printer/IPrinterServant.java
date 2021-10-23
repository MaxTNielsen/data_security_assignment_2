package com.printer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IPrinterServant extends Remote {

    public String echo(String cookie) throws RemoteException;

    public void print(String filename, String printer, String cookie) throws RemoteException;

    public void queue(String printer, String cookie) throws RemoteException;

    public void topQueue(String printer, int job, String cookie) throws RemoteException;

    public String start(String password, String username) throws RemoteException;

    public void stop(String cookie) throws RemoteException, InterruptedException;

    public void restart(String cookie) throws RemoteException;

    public void status(String printer, String cookie) throws RemoteException;

    public void readConfig(String parameter, String cookie) throws RemoteException;

    public void setConfig(String parameter, String value, String cookie) throws RemoteException;

    public boolean checkTimeStamp(Cookie c) throws RemoteException;

    public boolean authenticateCookie(String cookie) throws RemoteException;

    public void initialisePrinters() throws RemoteException;
}
