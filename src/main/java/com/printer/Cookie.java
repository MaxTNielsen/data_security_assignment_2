package com.printer;

public class Cookie {
    private final String id;
    private final long timestamp;

    public Cookie(){
        this.id = String.valueOf(Math.random());
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
