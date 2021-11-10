package com.printer;

import java.util.UUID;

public class Cookie {
    private final String id;
    private final long timestamp;

    public Cookie(){
        UUID uuid = UUID.randomUUID();
        this.id = String.valueOf(uuid);
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

    public String toString(){
        return "id: " + this.id + " timestamp: " + this.timestamp;
    }

}
