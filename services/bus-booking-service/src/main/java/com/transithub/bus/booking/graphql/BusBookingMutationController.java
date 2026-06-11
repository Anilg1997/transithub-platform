package com.transithub.bus.booking.graphql;
import com.transithub.bus.booking.service.BusBookingService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class BusBookingMutationController {
    private final BusBookingService service;
    public BusBookingMutationController(BusBookingService service) { this.service = service; }

    @MutationMapping
    public BusBookingService.BusBooking bookBus(@ContextValue UUID userId,
            @Argument String busId, @Argument String date, @Argument List<String> seats,
            @Argument String boardingPoint, @Argument String droppingPoint,
            @Argument List<Map<String, Object>> passengers) {
        return service.bookBus(userId, busId, date, seats, boardingPoint, droppingPoint, passengers);
    }

    @MutationMapping
    public BusBookingService.BusBooking cancelBus(@Argument String bookingRef) {
        return service.cancelBus(bookingRef);
    }
}
