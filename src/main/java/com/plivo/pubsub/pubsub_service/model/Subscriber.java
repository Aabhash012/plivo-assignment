package com.plivo.pubsub.pubsub_service.model;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ArrayBlockingQueue;

public class Subscriber {

    private final String clientId;
    private final WebSocketSession session;
    private final ArrayBlockingQueue<Message> queue;

    public Subscriber(String clientId, WebSocketSession session, int capacity) {
        this.clientId = clientId;
        this.session = session;
        this.queue = new ArrayBlockingQueue<>(capacity);
    }

    public String getClientId() { return clientId; }
    public WebSocketSession getSession() { return session; }
    public ArrayBlockingQueue<Message> getQueue() { return queue; }
}