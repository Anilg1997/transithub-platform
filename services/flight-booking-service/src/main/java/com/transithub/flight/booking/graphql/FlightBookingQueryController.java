package com.transithub.flight.booking.graphql;
import com.transithub.flight.booking.service.FlightBookingService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
public class FlightBookingQueryController {
    private final FlightBookingService service;
    public FlightBookingQueryController(FlightBookingService service) { this.service = service; }

    @QueryMapping
    public List<FlightBookingService.FlightBooking> myFlightBookings(@ContextValue UUID userId) {
        return service.getMyFlightBookings(userId);
    }

    @QueryMapping
    public FlightBookingService.FlightBooking flightBooking(@Argument String bookingRef) {
        return service.getFlightBooking(bookingRef);
    }
}
