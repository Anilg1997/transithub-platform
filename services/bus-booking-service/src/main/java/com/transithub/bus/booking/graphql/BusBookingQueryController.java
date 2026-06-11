package com.transithub.bus.booking.graphql;
import com.transithub.bus.booking.service.BusBookingService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
public class BusBookingQueryController {
    private final BusBookingService service;
    public BusBookingQueryController(BusBookingService service) { this.service = service; }

    @QueryMapping
    public List<BusBookingService.BusBooking> myBusBookings(@ContextValue UUID userId) {
        return service.getMyBusBookings(userId);
    }

    @QueryMapping
    public BusBookingService.BusBooking busBooking(@Argument String bookingRef) {
        return service.getBusBooking(bookingRef);
    }
}
