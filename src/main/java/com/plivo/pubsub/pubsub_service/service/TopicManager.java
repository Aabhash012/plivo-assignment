package com.plivo.pubsub.pubsub_service.service;

import com.plivo.pubsub.pubsub_service.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TopicManager {

    private final Map<String, Topic> topics = new ConcurrentHashMap<>();
    private final long startTime = System.currentTimeMillis();

    public Topic create(String name) {
        if (topics.containsKey(name)) throw new RuntimeException("exists");
        Topic t = new Topic(name);
        topics.put(name, t);
        return t;
    }

    public void delete(String name) {
        if (!topics.containsKey(name)) throw new RuntimeException("not found");
        topics.remove(name);
    }

    public Collection<Topic> list() {
        return topics.values();
    }

    public Topic get(String name) {
        return topics.get(name);
    }

    public long uptime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}