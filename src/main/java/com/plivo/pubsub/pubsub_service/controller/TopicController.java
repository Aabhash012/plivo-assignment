package com.plivo.pubsub.pubsub_service.controller;

import com.plivo.pubsub.pubsub_service.service.TopicManager;
import com.plivo.pubsub.pubsub_service.model.Topic;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TopicController {

    private final TopicManager manager;

    public TopicController(TopicManager manager) {
        this.manager = manager;
    }

    @PostMapping("/topics")
    public Map<String, String> create(@RequestBody Map<String,String> req) {
        manager.create(req.get("name"));
        return Map.of("status","created","topic",req.get("name"));
    }

    @DeleteMapping("/topics/{name}")
    public Map<String,String> delete(@PathVariable String name) {
        manager.delete(name);
        return Map.of("status","deleted","topic",name);
    }

    @GetMapping("/topics")
    public Map<String,Object> list() {
        List<Map<String,Object>> res = new ArrayList<>();
        for (Topic t : manager.list()) {
            res.add(Map.of(
                    "name", t.getName(),
                    "subscribers", t.getSubscribers().size()
            ));
        }
        return Map.of("topics",res);
    }

    @GetMapping("/health")
    public Map<String,Object> health() {
        int subs = manager.list().stream()
                .mapToInt(t -> t.getSubscribers().size()).sum();

        return Map.of(
                "uptime_sec", manager.uptime(),
                "topics", manager.list().size(),
                "subscribers", subs
        );
    }

    @GetMapping("/stats")
    public Map<String,Object> stats() {
        Map<String,Object> out = new HashMap<>();
        for (Topic t : manager.list()) {
            out.put(t.getName(),
                    Map.of("messages",t.getTotalMessages(),
                            "subscribers",t.getSubscribers().size()));
        }
        return Map.of("topics",out);
    }
}