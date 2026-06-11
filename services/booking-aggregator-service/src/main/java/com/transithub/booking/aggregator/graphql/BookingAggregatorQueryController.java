package com.transithub.booking.aggregator.graphql;
import com.transithub.booking.aggregator.service.BookingAggregatorService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class BookingAggregatorQueryController {
    private final BookingAggregatorService service;
    public BookingAggregatorQueryController(BookingAggregatorService service) { this.service = service; }

    @QueryMapping
    public List<BookingAggregatorService.TripOption> planTrip(@Argument List<Map<String, Object>> segments, @Argument Integer pax) {
        return service.planTrip(segments, pax);
    }

    @QueryMapping
    public List<BookingAggregatorService.MultiModalBooking> myBookings(@ContextValue UUID userId) {
        return service.getMyBookings(userId);
    }
}
