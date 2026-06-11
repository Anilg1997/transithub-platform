package com.transithub.train.booking.graphql;
import com.transithub.train.booking.service.TrainBookingService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
public class TrainBookingQueryController {
    private final TrainBookingService service;
    public TrainBookingQueryController(TrainBookingService service) { this.service = service; }

    @QueryMapping
    public List<TrainBookingService.TrainBooking> myTrainBookings(@ContextValue UUID userId) {
        return service.getMyTrainBookings(userId);
    }

    @QueryMapping
    public TrainBookingService.TrainBooking trainBooking(@Argument String bookingRef) {
        return service.getTrainBooking(bookingRef);
    }

    @QueryMapping
    public TrainBookingService.TrainBooking checkPnrStatus(@Argument String pnr) {
        return service.checkPnrStatus(pnr);
    }
}
