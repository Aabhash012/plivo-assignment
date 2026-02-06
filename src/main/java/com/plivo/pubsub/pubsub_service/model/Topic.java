package com.plivo.pubsub.pubsub_service.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Topic {

    private final String name;
    private final Map<String, Subscriber> subscribers = new ConcurrentHashMap<>();
    private final Deque<Message> history = new ArrayDeque<>();
    private final int HISTORY_LIMIT = 100;
    private long totalMessages = 0;

    public Topic(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public Map<String, Subscriber> getSubscribers() { return subscribers; }
    public long getTotalMessages() { return totalMessages; }

    public synchronized void addMessage(Message msg) {
        totalMessages++;
        if (history.size() == HISTORY_LIMIT) history.pollFirst();
        history.addLast(msg);
    }

    public synchronized List<Message> getLastN(int n) {
        List<Message> list = new ArrayList<>();
        Iterator<Message> it = history.descendingIterator();
        while (it.hasNext() && list.size() < n) {
            list.add(it.next());
        }
        Collections.reverse(list);
        return list;
    }
}