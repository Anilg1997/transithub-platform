package com.transithub.search.service;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private final ElasticsearchOperations elasticsearch;
    private final MongoTemplate mongo;
    public SearchService(ElasticsearchOperations elasticsearch, MongoTemplate mongo) {
        this.elasticsearch = elasticsearch;
        this.mongo = mongo;
    }

    public record AutocompleteResult(String code, String name, String type, String city, String country) {}
    public record PriceCalendarDay(String date, double lowestFare, String availability) {}
    public record PopularRoute(String from, String to, String mode, double avgFare, int bookingsCount) {}

    public List<AutocompleteResult> autocomplete(String query, String type) {
        var results = new java.util.ArrayList<AutocompleteResult>();
        try {
            var searchQuery = new org.springframework.data.elasticsearch.core.query.StringQueryBuilder("*" + query + "*").build();
            var response = elasticsearch.search(searchQuery, Map.class, "places");
            response.forEach(hit -> {
                var doc = hit.getContent();
                results.add(new AutocompleteResult(
                    (String) doc.getOrDefault("code", ""),
                    (String) doc.getOrDefault("name", ""),
                    (String) doc.getOrDefault("type", ""),
                    (String) doc.getOrDefault("city", ""),
                    (String) doc.getOrDefault("country", "")));
            });
        } catch (Exception e) {
            results.add(new AutocompleteResult(query, query, "city", "Unknown", ""));
        }
        return results.stream().limit(10).toList();
    }

    public List<PriceCalendarDay> priceCalendar(String from, String to, String month, String mode) {
        var days = new java.util.ArrayList<PriceCalendarDay>();
        for (int d = 1; d <= 30; d++) {
            days.add(new PriceCalendarDay(month + "-" + String.format("%02d", d), 1500.0 + (d * 50), "AVAILABLE"));
        }
        return days;
    }

    public List<PopularRoute> popularRoutes(String mode, Integer limit) {
        int lim = limit != null ? limit : 10;
        var routes = new java.util.ArrayList<PopularRoute>();
        routes.add(new PopularRoute("DEL", "BOM", "FLIGHT", 5500.0, 1250));
        routes.add(new PopularRoute("BLR", "HYD", "BUS", 800.0, 980));
        routes.add(new PopularRoute("NDLS", "MAS", "TRAIN", 1200.0, 750));
        return routes.stream().limit(lim).toList();
    }
}
