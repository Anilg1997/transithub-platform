package com.transithub.flight.booking.graphql;
import com.transithub.flight.booking.service.FlightBookingService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class FlightBookingMutationController {
    private final FlightBookingService service;
    public FlightBookingMutationController(FlightBookingService service) { this.service = service; }

    @MutationMapping
    public FlightBookingService.FlightBooking bookFlight(@ContextValue UUID userId,
            @Argument String flightId, @Argument String date, @Argument String cabinClass,
            @Argument List<Map<String, Object>> passengers, @Argument List<String> seatNumbers) {
        return service.bookFlight(userId, flightId, date, cabinClass, passengers, seatNumbers);
    }

    @MutationMapping
    public FlightBookingService.FlightBooking cancelFlight(@Argument String bookingRef) {
        return service.cancelFlight(bookingRef);
    }

    @MutationMapping
    public FlightBookingService.FlightBooking checkIn(@Argument String bookingRef, @Argument String pnr) {
        return service.checkIn(bookingRef, pnr);
    }
}
