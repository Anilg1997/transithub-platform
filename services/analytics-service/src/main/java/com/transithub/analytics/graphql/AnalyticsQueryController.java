package com.transithub.analytics.graphql;
import com.transithub.analytics.service.AnalyticsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class AnalyticsQueryController {
    private final AnalyticsService service;
    public AnalyticsQueryController(AnalyticsService s) { this.service = s; }

    @QueryMapping
    public List<AnalyticsService.DailyRevenue> dailyRevenue(@Argument String from, @Argument String to) {
        return service.getDailyRevenue(from, to);
    }

    @QueryMapping
    public List<AnalyticsService.BookingByMode> bookingsByMode(@Argument String from, @Argument String to) {
        return service.getBookingsByMode(from, to);
    }

    @QueryMapping
    public double cancellationRate(@Argument String from, @Argument String to) {
        return service.getCancellationRate(from, to);
    }

    @QueryMapping
    public int totalUsers() {
        return service.getTotalUsers();
    }

    @QueryMapping
    public List<AnalyticsService.PopularRoute> topRoutes(@Argument String mode, @Argument Integer limit) {
        return service.getTopRoutes(mode, limit != null ? limit : 10);
    }
}
