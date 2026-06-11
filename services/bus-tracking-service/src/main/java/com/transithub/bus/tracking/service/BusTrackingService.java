package com.transithub.bus.tracking.service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class BusTrackingService {
    private final MongoTemplate mongo;
    public BusTrackingService(MongoTemplate mongo) { this.mongo = mongo; }

    public record BusLocation(String busId, double latitude, double longitude, double speed,
                              String nextStop, String eta, String updatedAt) {}

    public BusLocation getBusLocation(String busId) {
        var doc = mongo.findOne(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("busId").is(busId)),
            Map.class, "bus_locations");
        if (doc == null) return null;
        return new BusLocation(
            (String) doc.getOrDefault("busId", busId),
            doc.get("latitude") instanceof Number n ? n.doubleValue() : 0.0,
            doc.get("longitude") instanceof Number n ? n.doubleValue() : 0.0,
            doc.get("speed") instanceof Number n ? n.doubleValue() : 0.0,
            (String) doc.getOrDefault("nextStop", ""),
            (String) doc.getOrDefault("eta", ""),
            (String) doc.getOrDefault("updatedAt", java.time.Instant.now().toString()));
    }

    public void publishBusLocation(String busId, double lat, double lng, double speed, String nextStop, String eta) {
        var update = new org.springframework.data.mongodb.core.query.Update()
            .set("busId", busId).set("latitude", lat).set("longitude", lng)
            .set("speed", speed).set("nextStop", nextStop).set("eta", eta)
            .set("updatedAt", java.time.Instant.now().toString());
        mongo.upsert(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("busId").is(busId)),
            update, "bus_locations");
    }
}
