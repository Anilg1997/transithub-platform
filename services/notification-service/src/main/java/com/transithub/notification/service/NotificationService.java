package com.transithub.notification.service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private final MongoTemplate mongo;
    public NotificationService(MongoTemplate mongo) { this.mongo = mongo; }

    public record Notification(String id, String type, String title, String message, boolean isRead,
                               String metadata, String createdAt) {}

    public List<Notification> getMyNotifications(String userId, Integer page, Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        var query = new Query(Criteria.where("userId").is(userId)).with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")).skip((long) p * s).limit(s);
        return mongo.find(query, Map.class, "notifications").stream()
            .map(doc -> new Notification(
                doc.get("_id") != null ? doc.get("_id").toString() : "",
                (String) doc.getOrDefault("type", ""),
                (String) doc.getOrDefault("title", ""),
                (String) doc.getOrDefault("message", ""),
                doc.get("isRead") instanceof Boolean b && b,
                (String) doc.get("metadata"),
                (String) doc.getOrDefault("createdAt", "")))
            .toList();
    }

    public int getUnreadCount(String userId) {
        return (int) mongo.count(new Query(Criteria.where("userId").is(userId).and("isRead").is(false)), "notifications");
    }

    public Notification markRead(String notificationId) {
        mongo.updateFirst(new Query(Criteria.where("_id").is(notificationId)), new Update().set("isRead", true), "notifications");
        var doc = mongo.findOne(new Query(Criteria.where("_id").is(notificationId)), Map.class, "notifications");
        if (doc == null) return null;
        return new Notification(doc.get("_id").toString(), (String) doc.getOrDefault("type", ""),
            (String) doc.getOrDefault("title", ""), (String) doc.getOrDefault("message", ""),
            true, (String) doc.get("metadata"), (String) doc.getOrDefault("createdAt", ""));
    }

    public boolean markAllRead(String userId) {
        mongo.updateMulti(new Query(Criteria.where("userId").is(userId).and("isRead").is(false)),
            new Update().set("isRead", true), "notifications");
        return true;
    }

    public void publishNotification(String userId, String type, String title, String message) {
        var doc = new java.util.HashMap<String, Object>();
        doc.put("userId", userId);
        doc.put("type", type);
        doc.put("title", title);
        doc.put("message", message);
        doc.put("isRead", false);
        doc.put("createdAt", java.time.Instant.now().toString());
        mongo.save(doc, "notifications");
    }
}
