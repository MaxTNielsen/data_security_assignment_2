package com.printer;

public class Cookie {
    private double id;
    private long timestamp;

    public Cookie(){
        this.id = Math.random();
        this.timestamp = System.currentTimeMillis();
    }

    public double getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
