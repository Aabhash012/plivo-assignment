# In-Memory Pub/Sub Service (WebSocket + REST)

This project implements a simplified in-memory Publish/Subscribe system using Spring Boot and WebSockets.

It supports real-time message delivery, topic management via REST APIs, concurrency safety, bounded backpressure handling, and message replay — all without using any external databases or brokers.

---

## 🚀 Features

### WebSocket Endpoint (`/ws`)

Supports the following operations:

- `subscribe` — subscribe a client to a topic
- `unsubscribe` — remove subscription
- `publish` — publish message to a topic
- `ping` — heartbeat check

Each published message is delivered to **all subscribers of the topic (fan-out)**.

---

### REST APIs

| Method | Endpoint | Description |
|-------|---------|------------|
| POST | `/topics` | Create topic |
| DELETE | `/topics/{name}` | Delete topic |
| GET | `/topics` | List topics with subscriber count |
| GET | `/health` | Uptime, topic count, subscribers |
| GET | `/stats` | Per-topic message stats |

---

## 🧠 Design Choices & Assumptions

### 1. In-Memory Only

- Topics stored in `ConcurrentHashMap`
- Subscribers stored per topic
- Messages kept in memory ring buffer

✅ No persistence across restarts (as required)

---

### 2. Concurrency Safety

- Thread-safe topic registry
- Concurrent subscriber management
- Synchronized replay buffer per topic

Supports:
- Multiple publishers
- Multiple subscribers
- Safe fan-out delivery

---

### 3. Backpressure Policy

Each subscriber has a bounded queue of **50 messages**.

If the queue is full:

→ The slow consumer is disconnected

This prevents:

- Memory overflow
- System slowdown

(Common pattern in pub/sub systems)

---

### 4. Message Replay (`last_n`)

Each topic stores the last **100 messages**.

On subscribe:

```json
{
  "type": "subscribe",
  "topic": "orders",
  "client_id": "s1",
  "last_n": 5
}


-----
### 5. Urls:
backend app url: https://plivo-assignment-z6t4.onrender.com
health check endpoint: https://plivo-assignment-z6t4.onrender.com/health
