package com.printer;

import com.google.gson.Gson;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class PrinterServant extends UnicastRemoteObject implements IPrinterServant {
    private IDB db;
    private Gson gson = new Gson();
    private long expire_time = 24 * 60 * 60 * 1000;
    //private HashMap<String, ArrayList<String>> printers = new HashMap<>(6);
    private Map<String, ArrayList<String>> printers = Collections.synchronizedMap(new HashMap<>());
    private boolean[] busy = new boolean[5];
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private FileWr fw;
    private Semaphore semaphore = new Semaphore(1);

    public PrinterServant(IDB db) throws IOException {
        super();
        initialisePrinters();
        this.db = db;
        this.fw = new FileWr();
    }

    synchronized ArrayList<String> getPrinterJobs(String printer) {
        return printers.get(printer);
    }

    boolean getFlag(int index) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b = busy[index];
        semaphore.release();
        return b;
    }

    void setFlag(int index, boolean b) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        busy[index] = b;
        semaphore.release();
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
            return checkTimeStamp(c);
        }
        return false;
    }

    @Override
    public void print(String filename, String printer, String cookie) throws RemoteException {
        if (authenticateCookie(cookie)) {
            int printerIndex = Integer.parseInt(String.valueOf(printer.toCharArray()[printer.length() - 1]));
            if (getFlag(printerIndex)) {
                getPrinterJobs(printer).add(filename);
            } else {
                getPrinterJobs(printer).add(filename);
                executor.execute(new printerThreads(printerIndex, filename, printer));
            }
        }
    }

    @Override
    public void queue(String printer, String cookie) throws RemoteException {
        if (authenticateCookie(cookie)) {
            fw.writeFile(printer+ " queue: " + printers.get(printer) + System.getProperty("line.separator"));
        }
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
        } else {
            return gson.toJson(c);
        }
        return gson.toJson(c);
    }

    @Override
    public void stop(String cookie) throws RemoteException {
        if (authenticateCookie(cookie)) {
            try {
                executor.shutdown();
                while (true) {
                    if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                        fw.cleanUp();
                        executor.shutdownNow();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        } else System.out.println("Session timeout\n Authenticate again");
        System.out.println("Server Closed.");
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
        return current - c.getTimestamp() <= expire_time;
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
            setFlag(id, true);
            try {
                while (getPrinterJobs(printer).size() > 0) {
                    Thread.sleep(2000);
                    fw.writeFile(getPrinterJobs(printer).get(0) + System.getProperty("line.separator"));
                    getPrinterJobs(printer).remove(0);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                setFlag(id, false);
            }
        }
    }

    @Override
    public String echo(String cookie) throws RemoteException {
        Cookie c = gson.fromJson(cookie, Cookie.class);
        if (db.authenticateCookie(c)) {
            if (checkTimeStamp(c))
                return "From Server";
        }
        return "From Server";
    }
}
