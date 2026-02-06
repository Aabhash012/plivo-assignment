package com.plivo.pubsub.pubsub_service.model;

public class Message {
    private String id;
    private Object payload;
    private long timestamp;

    public Message(String id, Object payload) {
        this.id = id;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public Object getPayload() { return payload; }
    public long getTimestamp() { return timestamp; }
}