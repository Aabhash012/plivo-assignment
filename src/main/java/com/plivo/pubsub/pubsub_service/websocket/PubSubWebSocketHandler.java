package com.plivo.pubsub.pubsub_service.websocket;

import com.plivo.pubsub.pubsub_service.model.*;
import com.plivo.pubsub.pubsub_service.service.TopicManager;
import com.plivo.pubsub.pubsub_service.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class PubSubWebSocketHandler extends TextWebSocketHandler {

    private final TopicManager manager;
    private static final int QUEUE_SIZE = 50;

    public PubSubWebSocketHandler(TopicManager manager) {
        this.manager = manager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage msg) throws Exception {

        Map<String,Object> req = JsonUtil.mapper.readValue(msg.getPayload(), Map.class);
        String type = (String) req.get("type");
        String topicName = (String) req.get("topic");

        if ("ping".equals(type)) {
            send(session, Map.of("type","pong","ts",System.currentTimeMillis()));
            return;
        }

        Topic topic = manager.get(topicName);
        if (topic == null) {
            send(session, Map.of("type","error","error",Map.of("code","TOPIC_NOT_FOUND")));
            return;
        }

        switch (type) {

            case "subscribe" -> {
                String cid = (String) req.get("client_id");
                Subscriber sub = new Subscriber(cid, session, QUEUE_SIZE);
                topic.getSubscribers().put(cid, sub);

                Integer lastN = (Integer) req.getOrDefault("last_n", 0);
                for (Message m : topic.getLastN(lastN)) {
                    deliver(sub, m, topicName);
                }

                send(session, Map.of("type","ack","topic",topicName));
            }

            case "unsubscribe" -> {
                String cid = (String) req.get("client_id");
                topic.getSubscribers().remove(cid);
                send(session, Map.of("type","ack","topic",topicName));
            }

            case "publish" -> {
                Map<String,Object> m = (Map<String,Object>) req.get("message");
                Message message = new Message(
                        (String)m.get("id"),
                        m.get("payload")
                );

                topic.addMessage(message);

                for (Subscriber s : topic.getSubscribers().values()) {
                    if (!s.getQueue().offer(message)) {
                        s.getSession().close();
                        continue;
                    }
                    deliver(s, message, topicName);
                }

                send(session, Map.of("type","ack","topic",topicName));
            }
        }
    }

    private void deliver(Subscriber sub, Message msg, String topic) throws Exception {
        send(sub.getSession(), Map.of(
                "type","event",
                "topic",topic,
                "message",Map.of(
                        "id",msg.getId(),
                        "payload",msg.getPayload()
                ),
                "ts",msg.getTimestamp()
        ));
    }

    private void send(WebSocketSession s, Object obj) throws Exception {
        s.sendMessage(new TextMessage(JsonUtil.mapper.writeValueAsString(obj)));
    }
}