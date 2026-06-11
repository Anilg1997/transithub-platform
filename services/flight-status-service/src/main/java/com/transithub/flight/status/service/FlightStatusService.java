package com.transithub.flight.status.service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FlightStatusService {
    private final MongoTemplate mongo;
    public FlightStatusService(MongoTemplate mongo) { this.mongo = mongo; }

    public record FlightStatus(String flightNumber, String scheduleDate, String status, int delayMinutes,
                               String gate, String updatedAt) {}

    public FlightStatus getFlightStatus(String flightNumber, String date) {
        var doc = mongo.findOne(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("flightNumber").is(flightNumber)
                    .and("scheduleDate").is(date)),
            Map.class, "flight_status");
        if (doc == null) return null;
        return new FlightStatus(
            (String) doc.getOrDefault("flightNumber", flightNumber),
            (String) doc.getOrDefault("scheduleDate", date),
            (String) doc.getOrDefault("status", "SCHEDULED"),
            doc.get("delayMinutes") instanceof Number n ? n.intValue() : 0,
            (String) doc.get("gate"),
            (String) doc.getOrDefault("updatedAt", java.time.Instant.now().toString()));
    }

    public void publishStatusUpdate(String flightNumber, String date, String status, int delayMinutes, String gate) {
        var update = new org.springframework.data.mongodb.core.query.Update()
            .set("flightNumber", flightNumber).set("scheduleDate", date)
            .set("status", status).set("delayMinutes", delayMinutes)
            .set("gate", gate).set("updatedAt", java.time.Instant.now().toString());
        mongo.upsert(
            org.springframework.data.mongodb.core.query.Query.query(
                org.springframework.data.mongodb.core.query.Criteria.where("flightNumber").is(flightNumber)
                    .and("scheduleDate").is(date)),
            update, "flight_status");
    }
}
