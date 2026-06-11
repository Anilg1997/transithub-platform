package com.transithub.booking.aggregator.graphql;
import com.transithub.booking.aggregator.service.BookingAggregatorService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class BookingAggregatorMutationController {
    private final BookingAggregatorService service;
    public BookingAggregatorMutationController(BookingAggregatorService service) { this.service = service; }

    @MutationMapping
    public BookingAggregatorService.MultiModalBooking createMultiModalBooking(@ContextValue UUID userId,
            @Argument List<Map<String, Object>> segments, @Argument List<Map<String, Object>> passengers) {
        return service.createMultiModalBooking(userId, segments, passengers);
    }
}
