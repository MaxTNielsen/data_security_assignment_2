package com.printer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import com.google.gson.Gson;

public class PrinterServant extends UnicastRemoteObject implements IPrinterServant {
    private IDB db;
    private Gson gson = new Gson();
    private long expire_time = 24 * 60 * 60 * 1000;

    public PrinterServant(IDB db) throws RemoteException {
        super();
        this.db = db;
    }

    //Test method
    @Override
    public String echo(String s, String cookie) throws RemoteException {
        // TODO check cookie valid
        Cookie c = db.authenticateCookie(gson.fromJson(cookie, Cookie.class));
        if (c != null)
            if (checkTimeStamp(c))
                return "From Server" + s;
        return "not valid";
    }

    @Override
    public void print(String filename, String parameter, String cookie) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void queue(String printer, String cookie) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void topQueue(String printer, int job, String cookie) {
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
    public void stop(String cookie) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void restart(String cookie) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void status(String printer, String cookie) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void readConfig(String parameter, String cookie) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void setConfig(String parameter, String value, String cookie) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean checkTimeStamp(Cookie c) throws RemoteException {
        long current = System.currentTimeMillis();

        if (current - c.getTimestamp() > expire_time)
            return false;
        return true;
        
    }
}
