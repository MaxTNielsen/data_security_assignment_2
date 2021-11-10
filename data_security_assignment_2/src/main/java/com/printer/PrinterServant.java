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
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    private static final int N_THREADS = 5;
    private Map<String, ArrayList<String>> printers;
    private Map<String, String> param = new HashMap<>();
    private boolean[] busy = new boolean[N_THREADS];
    private ExecutorService executor;
    private FileWr fw;
    private Map<String, String> cookie_user_map;
    private Semaphore[] sem = new Semaphore[]{new Semaphore(1),
            new Semaphore(1), new Semaphore(1), new Semaphore(1), new Semaphore(1)};

    public PrinterServant(IDB db) throws IOException {
        super();
        this.db = db;
        this.fw = new FileWr();
        cookie_user_map = new HashMap<>();
    }

    ArrayList<String> getPrinterJobs(String printer) {
        try {
            int printerIndex = Integer.parseInt(String.valueOf(printer.toCharArray()[printer.length() - 1]));
            sem[printerIndex].acquire();
            ArrayList<String> tempPrinters = printers.get(printer);
            sem[printerIndex].release();
            return tempPrinters;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean getFlag(int index) {
        try {
            sem[index].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b = busy[index];
        sem[index].release();
        return b;
    }

    void setFlag(int index, boolean b) {
        try {
            sem[index].acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        busy[index] = b;
        sem[index].release();
    }

    @Override
    public void initialisePrinters() throws RemoteException {
        printers = Collections.synchronizedMap(new HashMap<>());
        for (int i = 0; i < N_THREADS; i++) {
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
            Cookie c = gson.fromJson(cookie, Cookie.class);
            fw.writeFile("user "+ cookie_user_map.get(c.getId())+" call print to printer " + printer+"\n");
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
            Cookie c = gson.fromJson(cookie, Cookie.class);
            fw.writeFile("user "+ cookie_user_map.get(c.getId())+" call queue " + printer + " queue: " + printers.get(printer) + System.getProperty("line.separator"));
        }
    }

    @Override
    public void topQueue(String printer, int job, String cookie) throws RemoteException, IndexOutOfBoundsException, InterruptedException {
        String tempJob = null;
        if (authenticateCookie(cookie)) {
            int printerIndex = Integer.parseInt(String.valueOf(printer.toCharArray()[printer.length() - 1]));
            sem[printerIndex].acquire();
            if (printers.get(printer).size() > job) {
                tempJob = printers.get(printer).get(job);
                printers.get(printer).remove(job);
                printers.get(printer).add(0, tempJob);
            } else {
                System.out.println("Illegal argument");
            }
            sem[printerIndex].release();
        }
    }

    @Override
    public String start(String password, String username) throws RemoteException {
        //Authenticate client with password param
        Cookie c = null;
        if (db.authenticateUser(password, username)) {
            initialisePrinters();
            fw.setWriter();
            executor = Executors.newFixedThreadPool(N_THREADS);
            c = db.addCookieToDb();
            cookie_user_map.put(c.getId(), username);
        } else {
            return gson.toJson(c);
        }
        return gson.toJson(c);
    }

    public String start(String cookie) throws RemoteException {
        //Authenticate client with password param
        if (db.authenticateCookie(gson.fromJson(cookie, Cookie.class))) {
            initialisePrinters();
            fw.setWriter();
            executor = Executors.newFixedThreadPool(N_THREADS);
        } else {
            return gson.toJson(cookie);
        }
        return gson.toJson(cookie);
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
    public void restart(String cookie) throws RemoteException {
        stop(cookie);
        start(cookie);
    }

    @Override
    public void status(String printer, String cookie) throws RemoteException {
        if (authenticateCookie(cookie)) {
            int printerIndex = Integer.parseInt(String.valueOf(printer.toCharArray()[printer.length() - 1]));
            if (getFlag(printerIndex)) {
                fw.writeFile(printer + " is printing" + System.getProperty("line.separator"));
            } else {
                fw.writeFile(printer + " is not printing" + System.getProperty("line.separator"));
            }
        }
    }

    @Override
    public void readConfig(String parameter, String cookie) throws RemoteException {
        if (authenticateCookie(cookie)) {
            fw.writeFile(param.get(parameter) + System.getProperty("line.separator"));
        }
    }

    @Override
    public void setConfig(String parameter, String value, String cookie) throws RemoteException {
        if (authenticateCookie(cookie)) {
            Cookie c = gson.fromJson(cookie, Cookie.class);
            param.put(parameter, value);
            fw.writeFile("user" + cookie_user_map.get(c.getId())+ " set config " + param.get(parameter) + System.getProperty("line.separator"));
        }
    }

    @Override
    public boolean checkTimeStamp(Cookie c) throws RemoteException {
        long current = System.currentTimeMillis();
        return current - c.getTimestamp() <= EXPIRE_TIME;
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
}
