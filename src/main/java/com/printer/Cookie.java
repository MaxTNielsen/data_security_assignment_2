package com.printer;

public class Cookie {
    private final String id;
    private final long timestamp;

    public Cookie(){
        this.id = String.valueOf(Math.random());
        this.timestamp = System.currentTimeMillis();
    }

    public Cookie(String id, long timestamp)
    {
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }


}
