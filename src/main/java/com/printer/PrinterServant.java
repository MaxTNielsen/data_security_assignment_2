package com.printer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

public class PrinterServant extends UnicastRemoteObject implements IPrinterServant {
    private IDB db;
    private Gson gson = new Gson();
    private long expire_time = 24 * 60 * 60 * 1000;
    private HashMap<String, ArrayList<String>> printers = new HashMap<>(6);
    private boolean[] busy = new boolean[5];
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private FileWr fw;

    public PrinterServant(IDB db) throws IOException {
        super();
        initialisePrinters();
        this.db = db;
        this.fw = new FileWr();
    }

    synchronized ArrayList<String> getPrinterJobs(String printer) {
        return printers.get(printer);
    }

    @Override
    public void initialisePrinters() throws RemoteException {
        for (int i = 0; i < 5; i++) {
            printers.put("printer" + i, new ArrayList<>());
        }
    }

    @Override
    public boolean authenticateCookie(String cookie) throws RemoteException {
        Cookie c = gson.fromJson(cookie, Cookie.class);
        if (db.authenticateCookie(c)) {
            if (checkTimeStamp(c))
                return true;
        }
        return false;
    }

    @Override
    public void print(String filename, String printer, String cookie) throws RemoteException {
        System.out.println("Inside print");
        if (authenticateCookie(cookie)) {
            int printerIndex = Integer.parseInt(String.valueOf(printer.toCharArray()[printer.length()-1]) ) ;
            if (busy[printerIndex]) {
                getPrinterJobs(printer).add(filename);
            } else {
                executor.execute(new printerThreads(printerIndex, filename, printer));
            }
        }
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
        Cookie c = null;
        if (db.authenticateUser(password, username)) {
            c = db.addCookieToDb();
            //System.out.println("it worked!");
        } else {
            //System.out.println("it didnt work");
            return gson.toJson(c);
        }
        return gson.toJson(c);
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

    private class printerThreads extends Thread {
        int id;
        String filename;
        String printer;

        public printerThreads(int id, String filename, String printer) {
            this.id = id;
            this.filename = filename;
            this.printer = printer;
        }

        @Override
        public void run() {
            if (getPrinterJobs(printer).size() < 1) {
                busy[id] = true;
                try {
                    Thread.sleep(20000);
                    fw.writeFile(filename);
                    //System.out.println(filename);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                while (getPrinterJobs(printer).size() > 0)
                    busy[id] = true;
                try {
                    Thread.sleep(20000);
                    //System.out.println(getPrinterJobs(printer).get(0));
                    fw.writeFile(filename);
                    getPrinterJobs(printer).remove(0);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            busy[id] = false;
        }
    }

    //Test method
    @Override
    public String echo(String s, String cookie) throws RemoteException {
        Cookie c = gson.fromJson(cookie, Cookie.class);
        //System.out.println(c.toString());
        System.out.println("Did it" + " " + db.authenticateCookie(c));
        if (db.authenticateCookie(c)) {
            //System.out.println("It worked" + db.authenticateCookie(c));
            if (checkTimeStamp(c))
                System.out.println(System.currentTimeMillis() - c.getTimestamp());
            return "From Server";
        }
        return "From Server";
    }
}
