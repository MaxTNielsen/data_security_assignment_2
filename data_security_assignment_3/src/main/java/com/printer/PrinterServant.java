package com.printer;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.annotation.processing.Filer;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

public class PrinterServant extends UnicastRemoteObject implements IPrinterServant {
    private IDB db;
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    private static final int N_THREADS = 5;
    private Map<String, ArrayList<String>> printers;
    private Map<String, String> param = new HashMap<>();
    private boolean[] busy = new boolean[N_THREADS];
    private ExecutorService executor;
    private FileWr fw;
    private Semaphore[] sem = new Semaphore[]{new Semaphore(1),
            new Semaphore(1), new Semaphore(1), new Semaphore(1), new Semaphore(1)};
    private FileReader fr;
    private Map<String, ArrayList<String>> map;


    public PrinterServant(IDB db) throws IOException {
        super();
        this.db = db;
        this.fw = new FileWr();
        readFile();
    }

    private void readFile(){
        try {
            FileReader fr = new FileReader("access_control_policy.json");
            JsonElement root = new JsonParser().parse(fr);
            fr.close();
            JsonObject object = root.getAsJsonObject();
            this.map = new Gson().fromJson(object.toString(), Map.class);

            for(Map.Entry<String, ArrayList<String>> entry : map.entrySet()){
                System.out.println(entry.getKey() + " value " + entry.getValue());
            }

        }
         catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean CheckUserPermission(String username, String op){
        ArrayList<String> operation = map.get(op);
        if(operation.contains(username))
            return true;
        return false;
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
    public void print(String password, String username,String filename, String printer) throws RemoteException {
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "print")){    
                int printerIndex = Integer.parseInt(String.valueOf(printer.toCharArray()[printer.length() - 1]));
                
                fw.writeFile("user "+ username+" call print to printer " + printer+"\n");
                if (getFlag(printerIndex)) {
                    getPrinterJobs(printer).add(filename);
                } else {
                    getPrinterJobs(printer).add(filename);
                    executor.execute(new printerThreads(printerIndex, filename, printer));
                }
            }
        }
    }

    @Override
    public void queue(String password, String username,String printer) throws RemoteException {
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "queue")){
                fw.writeFile("user "+ username +" call queue " + printer + " queue: " + printers.get(printer) + System.getProperty("line.separator"));
            }
        }
    }

    @Override
    public void topQueue(String password, String username, String printer, int job) throws RemoteException, IndexOutOfBoundsException, InterruptedException {
        String tempJob = null;
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "topQueue")){
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
    }

    @Override
    public void start(String password, String username) throws RemoteException {
        //Authenticate client with password param
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "start")){
                initialisePrinters();
                fw.setWriter();
                executor = Executors.newFixedThreadPool(N_THREADS);
        }
    }
    }

    @Override
    public void stop(String password, String username) throws RemoteException {
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "stop")){
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
            }
        } else System.out.println("Authenticate again");
    }

    @Override
    public void restart(String password, String username) throws RemoteException {  
            stop(password, username);
            start(password, username);
    }

    @Override
    public void status(String password, String username, String printer) throws RemoteException {
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "status")){
                int printerIndex = Integer.parseInt(String.valueOf(printer.toCharArray()[printer.length() - 1]));
                if (getFlag(printerIndex)) {
                    fw.writeFile(printer + " is printing" + System.getProperty("line.separator"));
                } else {
                    fw.writeFile(printer + " is not printing" + System.getProperty("line.separator"));
                }
            }
        }
    }

    @Override
    public void readConfig(String password, String username, String parameter) throws RemoteException {
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "readConfig")){
                fw.writeFile(param.get(parameter) + System.getProperty("line.separator"));
        }
    }
    }

    @Override
    public void setConfig(String password, String username, String parameter, String value) throws RemoteException {
        if (db.authenticateUser(password, username)) {
            if(CheckUserPermission(username, "setConfig")){
                param.put(parameter, value);
                fw.writeFile("user" + username+ " set config " + param.get(parameter) + System.getProperty("line.separator"));
            }
        }
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
